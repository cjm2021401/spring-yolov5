package mayfFatory.mosaic.Service;

import com.jcraft.jsch.*;
import org.springframework.stereotype.Service;

import java.io.*;


@Service
public class sshConnectService {
    private String username ="root";
    private int port=0;
    private String host="";
    private String password = "";
    private String localFile = "./uploads/"; // 전송 파일 위치(로컬)
    private String serverPath = "/content/yolov5/"; // 대상 디렉토리(서버)
    public void setsshConnect(String host, String port, String password){
        this.host=host;
        this.port=Integer.parseInt(port);
        this.password=password;
    }
    public void connect(String Path) {
        localFile=localFile+Path;
        File file = new File(localFile);
        Session session = null;
        Channel inputChannel = null;
        Channel yoloChannel=null;
        ChannelSftp channelSftp = null;
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
            yoloChannel =session.openChannel("exec");
            ChannelExec channelExec=(ChannelExec) yoloChannel;

            System.out.println("=> Uploaded : " + file.getPath() + " at " + host);
            Upload(file, channelSftp);

            System.out.println("==> Detecting Object using yolov5" + host);
            goYolov5(channelExec, Path);
            Thread.sleep(2500);//wait for yolov5

            System.out.println("==>Get result file at"+host);
            getDetectedImage(channelSftp, Path);

            System.out.println("==>remove file at "+host);
            removeFile(channelExec, Path);

        }catch (Exception e){
            e.printStackTrace();;
        }finally {
            try {
                channelSftp.exit();
                yoloChannel.disconnect();
                inputChannel.disconnect();
                session.disconnect();
                file.delete();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        localFile="./uploads/";
    }

    public void removeFile(ChannelExec channelExec, String Path) throws Exception{
        channelExec.setCommand("cd /content/yolov5/; rm "+Path+"; rm -r runs");
        channelExec.connect();
    }

    public void getDetectedImage(ChannelSftp channelSftp, String Path) throws Exception{
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
    }

    public void Upload(File file, ChannelSftp channelSftp) throws  Exception{
            FileInputStream in =new FileInputStream(file);
            channelSftp.cd(serverPath);
            channelSftp.put(in, file.getName());
            in.close();
    }
    public void goYolov5(ChannelExec channelExec, String Path) throws Exception{

            channelExec.setCommand("cd /content/yolov5/; python3 detectAndmosaic.py --weights /content/yolov5/tattoocigar_lbest.pt --img 416 --conf 0.1 --source /content/yolov5/"+Path);
            channelExec.connect();
    }
}
