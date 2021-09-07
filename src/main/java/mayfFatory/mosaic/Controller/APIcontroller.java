package mayfFatory.mosaic.Controller;

import mayfFatory.mosaic.Service.sshConnectService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


@Controller
public class APIcontroller {

    private final sshConnectService sshConnectService;
    @Autowired
    public APIcontroller(sshConnectService sshConnectService){this.sshConnectService=sshConnectService;}

    private final String UPLOAD_DIR = "./uploads/";

    @GetMapping("/")
    public String homepage() {
        return "upload";
    }
    @GetMapping("/upload")
    public String uploadPage() {

        return "upload";
    }

    @GetMapping(value = "images/{imagename}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> userSearch(@PathVariable("imagename") String imagename) throws IOException {
        InputStream imageStream = new FileInputStream("./src/main/resources/static/images/" + imagename);
        File file=new File("./src/main/resources/static/images/" + imagename);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        if(file.exists()) file.delete();
        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("host") String host, @RequestParam("port") String port,
                             @RequestParam("password") String password, RedirectAttributes attributes, Model model) {
        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Try again with correct information");
            return "upload";
        }

        // normalize the file path
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        // save the file on the local file system
        try {
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            sshConnectService.setsshConnect(host,port,password);
            sshConnectService.connect(fileName);
            System.out.println(fileName);
            model.addAttribute("img_url", "images/"+fileName);
            return "result";
        } catch (IOException e) {
            e.printStackTrace();
            attributes.addFlashAttribute("message", "Try again with correct information");
            return "upload";
        }

    }


}
