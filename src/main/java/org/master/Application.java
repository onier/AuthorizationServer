package org.master;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**localhost:8090/oauth/authorize?client_id=clientauthcode&response_type=code&state=rensanning&redirect_uri=http://www.baidu.com
 * http://localhost:8090/oauth/token?grant_type=authorization_code&redirect_uri=http://www.baidu.com&code=D1Q6Nx
 * 
 * Basic Auth  clientauthcode/123456
 * 
 * http://localhost:8090/oauth/check_token?token=16bc6972-8ae4-477a-8603-60025e31bc3e
 * 
 * 
 * http://localhost:2018/api/admin?access_token=16bc6972-8ae4-477a-8603-60025e31bc3e
 * Created by kaenry on 2016/4/29.
 * Application BootStartup
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
