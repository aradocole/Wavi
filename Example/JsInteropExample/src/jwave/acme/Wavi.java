package jwave.acme;

import jwave.Transform;
import jwave.transforms.FastWaveletTransform;

@jsinterop.annotations.JsType
public class Wavi {
   public double[] waveAsDoubles;

   public float sampleRate;
   public int sampleSizeInBits;
   public int channels;
   public boolean signed;
   public boolean bigEndian;

   public int tLevel;

   public int wavelet;

   public int ogLength;

   public Wavi() {

   }

   public void setWaveArray(byte[] result) {
      int shift = result[0];

      sampleRate = (float) ((result[1] & 0xff) | ((result[2] << 8) & 0x0000FF00) | ((result[3] << 16) & 0x00ff0000) | ((result[4] << 24) & 0xff000000));
      sampleSizeInBits = result[5];
      channels = result[6];
      signed = result[7] == 1;
      bigEndian = result[8] == 1;
      //fileAudioFormat = new javax.sound.sampled.AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);

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
   }

   public void decompress() {
      Transform t = new Transform(new FastWaveletTransform(Operators.getWaveletFromIndex(wavelet)));

      waveAsDoubles = Operators.getDoubleArrayTruncated(t.reverse(Operators.getDoubleArrayOfCorrectLength(waveAsDoubles), tLevel), ogLength);
   }
}
