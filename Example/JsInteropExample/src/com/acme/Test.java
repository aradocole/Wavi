package com.acme;

@jsinterop.annotations.JsType
public class Test {
   public String made;
   public double[] waveAsDoubles;
   public byte[] waveAsBytes;
   public javax.sound.sampled.AudioFormat fileAudioFormat;

   public int tLevel;

   public int wavelet;

   public int ogLength;

   public Test(String ss) {
      made = ss;
   }

   public String make() {
      return "Yes! By Method.";
   }

   public void set(String s) {
      made = s;
   }

   public void setWave(String path) throws java.io.IOException {
      java.net.URL url = null;
      if (path.contains("zip")) {     //Determine if given path points to a compressed or uncompressed file, and load Wave.
         //<editor-fold desc="Read Wave from compressed ZIP.">
         if (path.contains("http")) {    //Determine if given path is a url, and load Wave.
            url = new java.net.URL(path);
         } else {
            url = new java.io.File(path).toURI().toURL();
         }
         //FileInputStream fis = new FileInputStream();
         java.io.InputStream in = url.openStream();

         java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new java.io.BufferedInputStream(in));

         java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();

         int read;
         byte[] buff = new byte[1024];
         while (zis.getNextEntry() != null) {
            while ((read = zis.read(buff)) > 0) {
               out.write(buff, 0, read);
            }
         }
         out.flush();

         byte[] result = out.toByteArray();

         int shift = result[0];

         float sampleRate = (float) ((result[1] & 0xff) | ((result[2] << 8) & 0x0000FF00) | ((result[3] << 16) & 0x00ff0000) | ((result[4] << 24) & 0xff000000));
         int sampleSizeInBits = result[5];
         int channels = result[6];
         boolean signed = result[7] == 1;
         boolean bigEndian = result[8] == 1;
         fileAudioFormat = new javax.sound.sampled.AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

         tLevel = result[9];

         wavelet = result[10];

         ogLength = ((result[11] & 0xff) | ((result[12] << 8) & 0x0000FF00) | ((result[13] << 16) & 0x00ff0000) | ((result[14] << 24) & 0xff000000));

         byte[] r = new byte[result.length - 15];
         for (int i = 15; i < result.length; i++) {
            r[i - 15] = result[i];
         }

         double[] doubles = new double[r.length/4];

         for (int i = 0; i < doubles.length; i++) {
            byte byteA = r[((i+1)*4)-4];
            byte byteB = r[((i+1)*4)-3];
            byte byteC = r[((i+1)*4)-2];
            byte byteD = r[((i+1)*4)-1];
            int intA = byteA & 0xff;
            int intB = (byteB << 8) & 0x0000FF00;
            int intC = (byteC << 16) & 0x00ff0000;
            int intD = (byteD << 24) & 0xff000000;
            if (shift == 0) {
               doubles[i] = (double) (intA | intB | intC | intD);
            }
            else {
               doubles[i] = ((double) (intA | intB | intC | intD)) / (Math.pow(10, shift-1));
            }
         }

         waveAsDoubles = doubles;

         waveAsBytes = toBytesFromDoubles(doubles);
         //</editor-fold>
      }
   }

   public byte[] toBytesFromDoubles(double[] f) {
        /*byte[] bytes = new byte[f.length*4];

        for (int i = 0; i < f.length; i++) {
            byte a = bytes[((i+1)*4)-4] = (byte)((int)f[i] & 0xff);
            byte b = bytes[((i+1)*4)-3] = (byte)(((int)f[i] >> 8) & 0xff);
            byte c = bytes[((i+1)*4)-2] = (byte)(((int)f[i] >> 16) & 0xff);
            byte d = bytes[((i+1)*4)-1] = (byte)(((int)f[i] >> 24) & 0xff);
        }
        if (fileAudioFormat != null) {
            AudioFormat format = new AudioFormat(fileAudioFormat.getSampleRate(), 32, fileAudioFormat.getChannels(), true, fileAudioFormat.isBigEndian());
            fileAudioFormat = format;
        }*/
      byte[] bytes = new byte[f.length*2];
      for (int i = 0; i < f.length; i++) {
         short s = (short) f[i];
         byte a = bytes[((i+1)*2)-2] = (byte)(s & 0xff);
         byte b = bytes[((i+1)*2)-1] = (byte)((s >> 8) & 0xff);
      }

      return bytes;
   }
}
