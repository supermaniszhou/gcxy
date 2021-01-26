import com.seeyon.ctp.common.security.MessageEncoder;

import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        MessageEncoder encoder=new MessageEncoder();
        String username="yanyi";
        String pwd=encoder.encode(username,"111111");//GBH0jrWV5l1+aQnrP8SglJHHyB4=guo
        System.out.println(pwd);
    }
}
