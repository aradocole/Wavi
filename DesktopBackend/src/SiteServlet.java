import JWave.Wave;
import com.google.api.client.extensions.appengine.http.UrlFetchTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.time.Clock;
import java.util.*;

public class SiteServlet extends HttpServlet{
    protected String BUCKET_NAME = "rd-site-resources";

    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
    .initialRetryDelayMillis(10)
    .retryMaxAttempts(10)
    .totalRetryPeriodMillis(15000)
    .build());

    protected Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    private static final int BUFFER_SIZE = 2*1024*1024;

    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    private GcsFilename getFileNameFromReq(HttpServletRequest req) {
        String[] splits = req.getRequestURI().split("/", 4);
        if (!splits[0].equals("") || !splits[1].equals("gcs")) {
            throw new IllegalArgumentException("The URL is not formed as expected. " +
                    "Expecting /gcs/<bucket>/<object>");
        }

        return new GcsFilename(splits[2], splits[3]);
    }

    protected GcsFilename getFileNameFromPath(String path) {
        if (path.equals("/")) {
            return new GcsFilename(BUCKET_NAME,  "home.html");
        }
        String file =  path.replace("/", "");
        if (path.contains(".")) {
            return new GcsFilename(BUCKET_NAME,  file);
        }
        return new GcsFilename(BUCKET_NAME,  file + ".html");
    }

    private void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = input.read(buffer);
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead);
                bytesRead = input.read(buffer);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if (req.getServerName().split("\\.")[0].equals("wavelets")) {
            BUCKET_NAME = "rd-site-resources/wavelets";
        }
        else if (req.getServerName().split("\\.")[0].equals("wavi")) {
            BUCKET_NAME = "rd-site-resources/wavelets";
        }
        else if (req.getServerName().split("\\.")[0].equals("harkness")) {
            BUCKET_NAME = "rd-site-resources/harkness";
        }
        else if (req.getServerName().split("\\.")[0].equals("interradio")) {
            BUCKET_NAME = "rd-site-resources/interradio";
        }
        else if (req.getServerName().split("\\.")[0].equals("right")) {
            BUCKET_NAME = "rd-site-resources/right";
        }
        else if (path.contains("/images/")) {
            BUCKET_NAME = "rd-site-resources/images";
            path = path.replace("/images/", "");
        }
        else if (path.contains("/songs/")) {
            BUCKET_NAME = "rd-site-resources/songs/";
            path = path.replace("/mops/songs/", "");
        }
        else if (path.contains("/assets")) {
            BUCKET_NAME = "rd-site-resources/harkness/assets";
            path = path.replace("/assets/", "");
        }
        else {
            BUCKET_NAME = "rd-site-resources";
        }

        GcsFilename fileName = getFileNameFromPath(path);
        String h = "text/"+ fileName.getObjectName().substring(fileName.getObjectName().lastIndexOf('.') + 1);
        if (h.contains("m4a")) {
            h = "audio/x-m4a";
        }
        if (h.contains("wav")) {
            h = "audio/wav";
        }
        if (h.contains("mp3")) {
            h = "application/octet-stream";
        }
        rsp.addHeader("Content-Type", h);
        GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
        copy(Channels.newInputStream(readChannel), rsp.getOutputStream());
    }

    public void doPost(HttpServletRequest req, HttpServletResponse rsp) throws ServletException, IOException {
        String response = "nope";
        String path = req.getRequestURI();
        if (path.equals("/songUpload/")) {
            String name = req.getQueryString().split("=")[1].split("&")[0];
            String id = req.getQueryString().split("=")[2];
            //String name = (String) getRequestParams(req).get("songName");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = req.getInputStream();

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
            out.flush();

            byte[] result = out.toByteArray();

            Wave w = null;
            try {
                w = new Wave(result);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
            w.setWavelet(39);
            w.compress(1500);

            BUCKET_NAME = "rd-site-resources/wavelets";
            GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
            String n = name +".zip";
            GcsFilename fileName = new GcsFilename(BUCKET_NAME, n);
            GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, instance);
            w.toZipStream(Channels.newOutputStream(outputChannel));

            String name2 = name.replaceAll(" ", "%20");
            String url = "https://storage.googleapis.com/rd-site-resources/wavelets/" + name2 +".zip";
            response = addSong(name, url, id);
        }
        //<editor-fold desc="songPost">
        if (path.equals("/songPost/")) {
            String name = (String) getRequestParams(req).get("songName");
            String url = (String) getRequestParams(req).get("URL");
            String id = (String) getRequestParams(req).get("id");


            response = addSong(name, url, id);
        }
        if (path.equals("/waviauth/")) {
            String idTokenString = (String) getRequestParams(req).get("id_token");

            JsonFactory js = JacksonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(UrlFetchTransport.getDefaultInstance(), js)
                    .setAudience(Collections.singletonList("380186301704-gmr2ctka1od78md38cogk48bmtrjlpnp.apps.googleusercontent.com")).build();

            GoogleIdToken idToken = null;
            try {
                idToken = verifier.verify(idTokenString);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();

                System.out.println("User ID: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = "test";//(String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");

                Key userKey = datastore.newKeyFactory().setKind("waviuser").newKey(userId);

                Entity user = datastore.get(userKey);

                boolean newUser = false;
                WaviUser Wuser;

                String[] songs = {"Stayin' Alive"};
                String[] URLS = {"https://storage.googleapis.com/rd-site-resources/wavelets/first20.zip"};
                List<StringValue> dSongs = new ArrayList<StringValue>();
                List<StringValue> dURLS = new ArrayList<StringValue>();
                for (int i = 0; i < songs.length; i++) {
                    dSongs.add(StringValue.of(songs[i]));
                    dURLS.add(StringValue.of(URLS[i]));
                }
                if (user == null) {
                    user = Entity.newBuilder(userKey)
                            .set("name", name)
                            .set("family_name", familyName)
                            .set("given_name", givenName)
                            .set("picture_URL", pictureUrl)
                            .set("email", email)
                            .set("email_verified", true)
                            .set("locale", locale)
                            .set("songNames", dSongs)
                            .set("songURLS", dURLS)
                            .build();
                    datastore.put(user);
                    newUser = true;
                    Wuser = new WaviUser(userId, name, familyName, givenName, pictureUrl, email, emailVerified, locale, Arrays.asList(songs), Arrays.asList(URLS));
                }
                else {
                    List<StringValue> s = user.getList("songNames");
                    List<StringValue> u = user.getList("songURLS");
                    List<String> S = new ArrayList<String>();
                    List<String> U = new ArrayList<String>();
                    for (int i = 0; i < s.size(); i++) {
                        S.add(s.get(i).get());
                        U.add(u.get(i).get());
                    }

                    Wuser = new WaviUser(userId, user.getString("name"), user.getString("family_name"), user.getString("given_name"),
                            user.getString("picture_URL"), user.getString("email"), user.getBoolean("email_verified"), user.getString("locale"),
                            S, U);
                }

                Gson a = new Gson();
                response = a.toJson(Wuser);
                // Use or store profile information
                // ...

            } else {
                System.out.println("Invalid ID token.");
            }
        }
        //</editor-fold>
        //<editor-fold desc="/right/">
        if (path.equals("/right/")) {
            String periodString = (String) getRequestParams(req).get("class_period");
            String nRights = (String) getRequestParams(req).get("rights");

            Key classKey = datastore.newKeyFactory().setKind("right_count").newKey(periodString);
            Entity right_class = datastore.get(classKey);

            List<StringValue> dates = new ArrayList<StringValue>();
            List<StringValue> counts = new ArrayList<StringValue>();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();

            if (right_class != null) {
                dates = right_class.getList("dates");
                counts = right_class.getList("counts");

                dates.add(StringValue.of(dateFormat.format(date)));
                counts.add(StringValue.of(nRights));

                right_class = Entity.newBuilder(classKey)
                        .set("dates", dates)
                        .set("counts", counts)
                        .build();

                datastore.put(right_class);

                response = "Date: " + dateFormat.format(date) + " Count: " + nRights;
            }
            else {
                dates.add(StringValue.of(dateFormat.format(date)));
                counts.add(StringValue.of(nRights));

                right_class = Entity.newBuilder(classKey)
                        .set("dates", dates)
                        .set("counts", counts)
                        .build();

                datastore.put(right_class);

                response = "Date: " + dateFormat.format(date) + " Count: " + nRights;
            }
        }
        //</editor-fold>
        //<editor-fold desc="rduser">
        if (path.equals("rduser")) {
            String idTokenString = (String) getRequestParams(req).get("idtoken");

            if (idTokenString != null) {
                JsonFactory js = JacksonFactory.getDefaultInstance();
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(UrlFetchTransport.getDefaultInstance(), js)
                        .setAudience(Collections.singletonList("380186301704-gmr2ctka1od78md38cogk48bmtrjlpnp.apps.googleusercontent.com")).build();

                GoogleIdToken idToken = null;
                try {
                    idToken = verifier.verify(idTokenString);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                }
                if (idToken != null) {
                    GoogleIdToken.Payload payload = idToken.getPayload();

                    // Print user identifier
                    String userId = payload.getSubject();

                    // Get profile information from payload
                    String email = payload.getEmail();
                    boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                    String name = (String) payload.get("name");
                    String pictureUrl = (String) payload.get("picture");
                    String familyName = (String) payload.get("family_name");
                    String givenName = (String) payload.get("given_name");

                    Key userKey = datastore.newKeyFactory().setKind("rduser").newKey(userId);

                    Entity user = datastore.get(userKey);

                    RdUser rduser;
                    if (user == null) {
                        user = Entity.newBuilder(userKey)
                                .set("name", name)
                                .set("family_name", familyName)
                                .set("given_name", givenName)
                                .set("picture_URL", pictureUrl)
                                .set("email", email)
                                .set("email_verified", emailVerified)
                                .set("balance", 0.00)
                                .build();

                        datastore.put(user);
                        //rduser = new RdUser(userId, name, familyName, givenName, pictureUrl, email, emailVerified, );
                    } else {
                        //RdUser rduser = new RdUser(userId, user.getString("name"), user.getString("family_name"), user.getString("given_name"),
                               // user.getString("picture_URL"), user.getString("email"), user.getBoolean("email_verified"), user.getDouble("balance"));
                    }
                }
            }
            else {
                String userId = (String) getRequestParams(req).get("user_id");
                Key userKey = datastore.newKeyFactory().setKind("rduser").newKey(userId);
                Entity user = datastore.get(userKey);

                RdUser rduser = new RdUser(userId, user.getString("name"), user.getString("family_name"), user.getString("given_name"),
                        user.getString("picture_URL"), user.getString("email"), user.getBoolean("email_verified"), user.getDouble("balance"));

                Gson a = new Gson();
                response = a.toJson(rduser);
            }
        }
        //</editor-fold>
        //<editor-fold desc="/user/">
        if (path.equals("/user/")) {
            String userId = (String) getRequestParams(req).get("user_id");
            Key userKey = datastore.newKeyFactory().setKind("user").newKey(userId);
            Entity user = datastore.get(userKey);

            HarknessUser Huser = new HarknessUser(userId, user.getString("name"), user.getString("family_name"), user.getString("given_name"),
                    user.getString("picture_URL"), user.getString("email"), user.getBoolean("email_verified"), user.getString("locale"),
                    user.getBoolean("first_time"), user.getString("class_id"), user.getBoolean("evelated"));

            Gson a = new Gson();
            response = a.toJson(Huser);
        }
        //</editor-fold>
        //<editor-fold desc="/map/">
        if (path.equals("/map/")) {
            String mapID = (String) getRequestParams(req).get("map_id");

            Key mapKey = datastore.newKeyFactory().setKind("map").newKey(mapID);

            Entity hark_map = datastore.get(mapKey);

            if (hark_map != null) {
                HarknessMap harknessMap = new HarknessMap(hark_map.getString("date"), hark_map.getString("topic"), hark_map.getString("mapper_id"), hark_map.getList("user_ids"),
                        hark_map.getList("topUsers"), hark_map.getList("bottomUsers"), hark_map.getList("leftUsers"), hark_map.getList("rightUsers"), (int) hark_map.getDouble("namereferences"),
                        (int) hark_map.getDouble("textreferences"), hark_map.getString("grade"), hark_map.getString("totaltime"), hark_map.getList("topics"), hark_map.getList("responses"));

                Gson a = new Gson();
                response = a.toJson(harknessMap);
            }
            else {
                Date d = new Date();
                //HarknessMap hmap = new HarknessMap(sdf.format(d), (String) getRequestParams(req).get("topic"), (String) getRequestParams(req).get("mapper_id"), (List) getRequestParams(req).get("user_ids"), );
            }
        }
        //</editor-fold>
        //<editor-fold desc="/class/">
        if(path.equals("/class/")) {
            String classID = (String) getRequestParams(req).get("class_id");

            Key classKey = datastore.newKeyFactory().setKind("class").newKey(classID);

            Entity hark_class = datastore.get(classKey);

            if (hark_class != null) {
                String[] sids = new String[hark_class.getList("student_ids").size()];
                for (int i = 0; i < hark_class.getList("student_ids").size(); i++) {
                    sids[i] = hark_class.getList("student_ids").get(i).get().toString();
                }

                String[] mids = new String[hark_class.getList("map_ids").size()];
                for (int i = 0; i < hark_class.getList("map_ids").size(); i++) {
                    mids[i] = hark_class.getList("map_ids").get(i).get().toString();
                }

                HarknessClass harknessClass = new HarknessClass(hark_class.getString("class_code"), hark_class.getString("instructor_id"), hark_class.getString("class_name"),
                        sids, mids);
                Gson g = new Gson();
                response = g.toJson(harknessClass);
            }
            else {
                String[] ids = {"102177347718020988482", "108272897312241103615", "112761193552571440780", "114804277647987142956", "115220576548523710117"};
                String[] maps = {"fffff001"};
                HarknessClass hclass = new HarknessClass(classID, "1", "", ids, maps);
                List<StringValue> sids = new ArrayList<StringValue>();
                for (int i = 0; i < hclass.student_ids.length; i++) {
                    sids.add(StringValue.of(hclass.student_ids[i]));
                }
                List<StringValue> mids = new ArrayList<StringValue>();
                for (int i = 0; i < hclass.map_ids.length; i++) {
                    mids.add(StringValue.of(hclass.map_ids[i]));
                }
                hark_class = Entity.newBuilder(classKey)
                        .set("class_code", classID)
                        .set("instructor_id", hclass.instructor_id)
                        .set("class_name", hclass.class_name)
                        .set("student_ids", sids)
                        .set("map_ids", mids)
                        .build();
                datastore.put(hark_class);
                Gson a = new Gson();
                response = a.toJson(hclass);
            }
        }
        //</editor-fold>
        //<editor-fold desc="/userauth/">
        if (path.equals("/userauth/")) {
            String idTokenString = (String) getRequestParams(req).get("idtoken");

            JsonFactory js = JacksonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(UrlFetchTransport.getDefaultInstance(), js)
                    .setAudience(Collections.singletonList("380186301704-gmr2ctka1od78md38cogk48bmtrjlpnp.apps.googleusercontent.com")).build();

            GoogleIdToken idToken = null;
            try {
                idToken = verifier.verify(idTokenString);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();

                System.out.println("User ID: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = "test";//(String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");

                Key userKey = datastore.newKeyFactory().setKind("user").newKey(userId);

                Entity user = datastore.get(userKey);

                boolean newUser = false;
                HarknessUser Huser;
                if (user == null) {
                    user = Entity.newBuilder(userKey)
                            .set("name", name)
                            .set("family_name", familyName)
                            .set("given_name", givenName)
                            .set("picture_URL", pictureUrl)
                            .set("email", email)
                            .set("email_verified", true)
                            .set("locale", locale)
                            .set("class_id", "fffff")
                            .set("evelated", false)
                            .set("first_time", false)
                            .build();

                    datastore.put(user);
                    newUser = true;
                    Huser = new HarknessUser(userId, name, familyName, givenName, pictureUrl, email, emailVerified, locale, newUser, "fffff", false);
                }
                else {
                   Huser = new HarknessUser(userId, user.getString("name"), user.getString("family_name"), user.getString("given_name"),
                        user.getString("picture_URL"), user.getString("email"), user.getBoolean("email_verified"), user.getString("locale"),
                           user.getBoolean("first_time"), user.getString("class_id"), user.getBoolean("evelated"));
                }

                Gson a = new Gson();
                response = a.toJson(Huser);
                // Use or store profile information
                // ...

            } else {
                System.out.println("Invalid ID token.");
            }
        }
        //</editor-fold>
        //<editor-fold desc="chunk">
        if (path.equals("/chunk/")) {
            String song = (String) getRequestParams(req).get("song");

            BUCKET_NAME = "rd-site-resources/wavelets";
            GcsFilename file = new GcsFilename(BUCKET_NAME, song);
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(file, 0, BUFFER_SIZE);
            InputStream in = Channels.newInputStream(readChannel);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            BUCKET_NAME = "rd-site-resources/wavelets/" + song.substring(0, song.indexOf("."));

            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
            out.flush();

            byte[] result = out.toByteArray();

            GcsFileOptions instance = GcsFileOptions.getDefaultInstance();

            int spot = 1;
            byte[] buf = new byte[100000];
            for (int i = 0; i < result.length; i++) {
                if (i < 100000*spot) {
                    buf[i-(100000*(spot - 1))] = result[i];
                }
                if (i == spot*100000) {
                    InputStream bis = new ByteArrayInputStream(buf);
                    GcsOutputChannel outputChannel;
                    GcsFilename fileName = new GcsFilename(BUCKET_NAME, song.substring(0, song.indexOf(".")) + "_" + spot + ".wav");
                    outputChannel = gcsService.createOrReplace(fileName, instance);
                    copy(bis, Channels.newOutputStream(outputChannel));

                    spot++;
                    if ((spot*100000) > result.length) {
                        buf = new byte[result.length - (100000 * (spot - 1))];
                    }
                }
                if (i == (result.length - 1)) {
                    InputStream bis = new ByteArrayInputStream(buf);
                    GcsOutputChannel outputChannel;
                    GcsFilename fileName = new GcsFilename(BUCKET_NAME, song.substring(0, song.indexOf(".")) + "_" + spot + ".wav");
                    outputChannel = gcsService.createOrReplace(fileName, instance);
                    copy(bis, Channels.newOutputStream(outputChannel));
                }
            }


        }
        //</editor-fold>
        //<editor-fold desc="seg">
        if (path.equals("/seg/")) {
            String id = (String) getRequestParams(req).get("id");
            String seg = (String) getRequestParams(req).get("seg");

            BUCKET_NAME = "rd-site-resources/wavelets/" + id.substring(0, id.indexOf("."));
            GcsFilename file = new GcsFilename(BUCKET_NAME, id.substring(0, id.indexOf(".")) + "_" + seg + ".wav");
            response = file.getObjectName();
            rsp.addHeader("Content-Type", "audio/wav");
            GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(file, 0, BUFFER_SIZE);
            copy(Channels.newInputStream(readChannel), rsp.getOutputStream());
        }
        //</editor-fold>
        //<editor-fold desc="upload">
        if (path.equals("/upload/")) {
            String name = (String) getRequestParams(req).get("name");

            BUCKET_NAME = "rd-site-resources/wavelets";
            GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
            GcsFilename fileName = new GcsFilename(BUCKET_NAME, name);
            GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, instance);
            copy(req.getInputStream(), Channels.newOutputStream(outputChannel));
        }
        //</editor-fold>
        if (path.equals("/compress/")) {
            String name = (String) getRequestParams(req).get("name");

            compress(name);
        }
        if (path.equals("/list/")) {
            
        }
        else {
            doResponse(req, rsp, response);
        }
    }

    public void compress(String file) throws IOException {
        Wave w = null;
        try {
            w = new Wave("https://storage.googleapis.com/rd-site-resources/wavelets/" + file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        w.setWavelet(39);
        w.compress(1500);

        BUCKET_NAME = "rd-site-resources/wavelets";
        GcsFileOptions instance = GcsFileOptions.getDefaultInstance();
        String n = file.substring(0, file.indexOf(".")) + ".zip";
        GcsFilename fileName = new GcsFilename(BUCKET_NAME, n);
        GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName, instance);
        w.toZipStream(Channels.newOutputStream(outputChannel));
    }

    public void doOptions(HttpServletRequest  req, HttpServletResponse rsp) throws ServletException, IOException {
        doResponse(req, rsp, "OK");
    }

    public void doResponse(HttpServletRequest req, HttpServletResponse rsp, Object response) throws ServletException, IOException {
        // enable cors                         //
        rsp.addHeader("Access-Control-Allow-Origin",  "*");
        rsp.addHeader("Access-Control-Max-Age",       "1800");
        //rsp.addHeader("Content-Type", "text/html");

        // support all requested headers       //
        for (Enumeration<String> ctlReqHeaders =
             req.getHeaders("Access-control-request-headers");
             ctlReqHeaders.hasMoreElements();)
        {
            rsp.addHeader("Access-Control-Allow-Headers", ctlReqHeaders.nextElement());
        }
        if (response instanceof Throwable)
        {
            rsp.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.toString());
        }
        else
        {
            rsp.getWriter().print(response);
        }
    }

    public Map<String,Object> getRequestParams (HttpServletRequest req) {

        Map<String,String[]> paramsRaw = req.getParameterMap();
        Map<String,Object>   params    = new HashMap<String,Object>();

        for (String key : paramsRaw.keySet()) {
            params.put(key, paramsRaw.get(key)[0]);
        }

        return(params);
    }

    public String addSong(String name, String url, String id) {
        Key userKey = datastore.newKeyFactory().setKind("waviuser").newKey(id);

        Entity user = datastore.get(userKey);

        WaviUser Wuser;
        List<StringValue> s = new ArrayList<StringValue>(user.getList("songNames"));
        List<StringValue> u = new ArrayList<StringValue>(user.getList("songURLS"));

        s.add(StringValue.of(name));
        u.add(StringValue.of(url));

        List<String> S = new ArrayList<String>();
        List<String> U = new ArrayList<String>();
        for (int i = 0; i < s.size(); i++) {
            S.add(s.get(i).get());
            U.add(u.get(i).get());
        }

        Entity nuser = Entity.newBuilder(userKey)
                .set("name", user.getString("name"))
                .set("family_name", user.getString("family_name"))
                .set("given_name", user.getString("given_name"))
                .set("picture_URL", user.getString("picture_URL"))
                .set("email", user.getString("email"))
                .set("email_verified", user.getBoolean("email_verified"))
                .set("locale", user.getString("locale"))
                .set("songNames", s)
                .set("songURLS", u)
                .build();
        datastore.put(nuser);

        Wuser = new WaviUser(id, user.getString("name"), user.getString("family_name"), user.getString("given_name"),
                user.getString("picture_URL"), user.getString("email"), user.getBoolean("email_verified"), user.getString("locale"),
                S, U);

        Gson a = new Gson();
        return a.toJson(Wuser);
    }
}
