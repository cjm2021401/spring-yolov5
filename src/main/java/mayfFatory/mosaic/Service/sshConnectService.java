package mayfFatory.mosaic.Service;

import com.jcraft.jsch.*;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
public class sshConnectService {
    private String username ="root";
    private int port=15419;
    private String host="8.tcp.ngrok.io";
    private String password = "mayf";
    private String localFile = "./uploads/"; // 전송 파일 위치(로컬)
    private String serverPath = "/content/yolov5/"; // 대상 디렉토리(서버)


    public void connect(String Path) {
        localFile=localFile+Path;
        File file = new File(localFile);
        Session session = null;
        Channel inputChannel = null;
        Channel yoloChannel=null;
        ChannelSftp channelSftp = null;
        FileInputStream in = null;
        JSch jsch = new JSch();
        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no"); // 인증서 검사를 하지 않음

            session.setConfig(config);
            session.connect();

            inputChannel = session.openChannel("sftp");
            inputChannel.connect();

            // 채널을 SSH용 채널 객체로 캐스팅
            channelSftp = (ChannelSftp) inputChannel;
            System.out.println("==> Connected to" + host);
            in =new FileInputStream(file);

            channelSftp.cd(serverPath);
            channelSftp.put(in, file.getName());

            System.out.println("=> Uploaded : " + file.getPath() + " at " + host);


            yoloChannel =session.openChannel("exec");
            ChannelExec channelExec=(ChannelExec) yoloChannel;

            System.out.println("==> Connected to" + host);
            channelExec.setCommand("cd /content/yolov5/; python3 detect.py --weights /content/yolov5/3000Image_YOLOv5s_best.pt --img 416 --conf 0.1 --source /content/yolov5/"+Path);
            channelExec.connect();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("==> Detecting Object using yolov5" + host);

            channelSftp.cd(serverPath+"runs/detect/exp/");
            InputStream is=null;
            FileOutputStream out=null;
            try {
                is=channelSftp.get(Path);
            }catch (SftpException e){
                e.printStackTrace();
            }

            try {

                out = new FileOutputStream( new File("./src/main/resources/static/images/"+Path) );
                int i;
                while ((i = is.read()) != -1) {
                    out.write(i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                out.close();
                is.close();
            }
            channelExec.setCommand("cd /content/yolov5/; rm "+Path+"; rm -r runs");
            channelExec.connect();

        }catch (Exception e){
            e.printStackTrace();;
        }finally {
            try {
                in.close();
                channelSftp.exit();
                yoloChannel.disconnect();
                inputChannel.disconnect();
                session.disconnect();
                file.delete();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }




}
