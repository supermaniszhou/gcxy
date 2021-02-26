import com.seeyon.ctp.common.security.MessageEncoder;

import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        MessageEncoder encoder = new MessageEncoder();
        String pwd = encoder.encode("yaner", "666666");
        System.out.println(pwd);
    }
}
