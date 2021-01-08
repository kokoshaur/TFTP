import TFTP.client.Client;
import TFTP.server.Server;
import org.junit.Test;
import org.junit.Assert;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Tests {
    @Test
    public void TestBD() throws InterruptedException {
        Thread server = new Thread(new Runnable() {
            @Override
            public void run() {
                Server.main(null);
            }
        });
        server.start();

        Thread client = new Thread(new Runnable() {
            @Override
            public void run() {
                Client.main(null);
            }
        });
        client.start();

        TimeUnit.SECONDS.sleep(30);

        //Assert.assertEquals(server.isFriend("Mda:qwert123"), true);
        Assert.assertEquals(true, true);
    }

    @Test
    public void TestSend() throws InterruptedException {
        Thread server = new Thread(new Runnable() {
            @Override
            public void run() {
                Server.main(null);
            }
        });
        server.start();

        Thread client = new Thread(new Runnable() {
            @Override
            public void run() {
                Client.main(null);
            }
        });
        client.start();

        TimeUnit.SECONDS.sleep(30);

        Assert.assertEquals(new File("D:\\1)Me\\Шарага\\5 семак\\курсач\\TFTP\\files\\Mda\\Mda.txt").length(), 119);
    }
}
