
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class TrackerServer extends Thread{

    static Tracker tracker;

    static int clientN=0;
    static ServerSocket serverSocket;
    static Socket socket;

    public TrackerServer(Socket socket,int clientN) {
        this.clientN = clientN;
        this.socket = socket;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, URISyntaxException, InterruptedException  {
        serverSocket = new ServerSocket(4444);
        tracker = new Tracker(serverSocket.getInetAddress());

        FilenameFilter filenameFilter = (dir, name) -> name.endsWith(".torrent");

        for(File file: new File("D://IdeaProjects//MyTorrentTracker//torrents").listFiles(filenameFilter)){
            tracker.announce(TrackedTorrent.load(file));
        }
        tracker.start();

        while(true){
            socket= serverSocket.accept();
            new TrackerServer(socket,clientN).start();
            clientN++;
        }
    }

    @Override
    public void run(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true){
                    String newTorrent = in.readLine();
                    if(newTorrent!=null){
                        System.out.println(newTorrent);
                        File file =createTorrentFile(newTorrent);
                        tracker.announce(TrackedTorrent.load(file));
                        tracker.stop();
                        tracker.start();
                    }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static File createTorrentFile(String sharedFilePath) throws URISyntaxException, InterruptedException, NoSuchAlgorithmException, IOException {
        File sF = new File(sharedFilePath);
        FileUtils.copyFileToDirectory(sF,new File("D://IdeaProjects//MyTorrentTracker//torrents//"));
       /* Torrent torrent = Torrent.create(sF,tracker.getAnnounceUrl().toURI(),"createByHelge");
        FileOutputStream fileOutputStream = new FileOutputStream("D://IdeaProjects//MyTorrentTracker//torrents//"+sF.getName());
        torrent.save(fileOutputStream);
        fileOutputStream.close();*/
        return sF;
    }
}