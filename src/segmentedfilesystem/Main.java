package segmentedfilesystem;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {

        public static void main(String[] args) throws IOException, InterruptedException {
                BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
                DatagramSocket socket = new DatagramSocket();
                InetAddress IPAddress = InetAddress.getByName("axiom");

                byte[] sendData = new byte[1028];

                String sentence = input.readLine();
                sendData = sentence.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6014);
                socket.send(sendPacket);
                //use HashSet to store all packets, prevent duplicates
                Set<byte[]> packetArr = new HashSet<byte[]>();
                //Base on the code in :http://wiki.jikexueyuan.com/project/java-socket/udp.html
                //we tried to set up the longest time for receiving data block to 5000
                //if time out more then 3 times, we assume that there is no buffer sending from the server side anymore
                //and stop receiving, close client socket.
                //
                socket.setSoTimeout(5000);
                int tries=0;
                boolean receivedResponse = false;
                //int n = 0;
                while(!receivedResponse && tries < 3){
                //while (true) {
                        try{
                                
                                byte[] receiveData = new byte[1028];
                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                socket.receive(receivePacket);
                                receivedResponse = true;
                                if(receivedResponse){
                                        packetArr.add(receivePacket.getData());
                                        receivedResponse = false;
                                }

                        }catch (InterruptedIOException e){
                                tries +=1;
                                System.out.println("Time out,"+ (3-tries) + " more tries");
                        }
//                              if (receiveData[0] % 4 == 3) {
//                                      n = n + (((receiveData[2] & 0xff) << 8) | (receiveData[3] & 0xff));
//                              }
//                              if (packetArr.size() == 134){
//                                      break;
//                              }
                }
                socket.close();

                //store all the packets in an new arr and use a for loop to change the type of subarray into ByteArr type
                Object[] arr = packetArr.toArray();
                ByteArr[] NewArray = new ByteArr[packetArr.size()];

                for (int i = 0; i < packetArr.size(); i++) {
                        NewArray[i] = new ByteArr((byte[]) arr[i]);
                }

                //sort NewArray base on fileID first, then packetID
                Arrays.sort(NewArray);
                System.out.println(NewArray.length);

                FileOutputStream out=null;
                String str="";
                for (int i = 0; i < packetArr.size(); i++) {
                        arr[i] = NewArray[i].b;
                        byte[] f = (byte[]) arr[i];
                        if (f[0] % 2 == 0) {
                                byte[] ch = Arrays.copyOfRange(f, 2, f.length);
                                str = new String(ch).trim();
                                System.out.println("Opening file: " + str );
                                File fileThing = new File(str);
                                out = new FileOutputStream(fileThing);
                        } else {
                                byte[] temp = Arrays.copyOfRange(f, 4, f.length);
                                        try{
                                                if(f[0] % 4 == 3){
                                                        int end = temp.length;
                                                        for(int k=temp.length-1; k > 0; k--){
                                                                if(temp[k] != 0){
                                                                        end = k;
                                                                        break;
                                                                }
                                                        }
                                                        System.out.println((((f[2] & 0xff)<<8)|(f[3] & 0xff)));
                                                        out.write(Arrays.copyOfRange(temp, 0, end+1));
                                                        out.flush();
                                                        System.out.println("Closing file: " + str);
                                                        out.close();
                                                }else{
                                                System.out.println((((f[2] & 0xff)<<8)|(f[3] & 0xff)));
                                                out.write(temp);
                                                out.flush();}
                                        }catch(IOException e){

                                        }
                        }
                }
        }
}
