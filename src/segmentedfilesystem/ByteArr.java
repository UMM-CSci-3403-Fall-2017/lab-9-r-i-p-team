package segmentedfilesystem;
  
import java.util.Arrays;

public class ByteArr implements Comparable<ByteArr>{
        public int n;
        public byte[] b;
        public ByteArr(byte[] c){
                b = c;
                if(c[0]%2==0){
                n = c[1]*100000-1;
                }else{
                n = c[1]*100000+(((c[2] & 0xff)<<8)|(c[3] & 0xff));
                }
        }

        @Override
        public int compareTo(ByteArr o) {
                return n - o.n;
        }
}
