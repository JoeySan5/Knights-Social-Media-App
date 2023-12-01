package edu.lehigh.cse216.knights.backend;

//https://github.com/googleapis/java-storage/tree/main
//Imports are from above^, collected to make authenticated requests to google cloud
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeoutException;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

/**
 * DatabaseTest provides unit tests for the Idea database entry.
 */
public class cloudTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public cloudTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(IdeaTest.class);
    }

    /**
     * Ensure the correct value is being stored and retrieved from the memecache
     */
    public void memecacheEx() {
        String key = "key";
        String value = "value";
        // conncecting to cache
        MemcachedClient mc = null;

        try {
            mc = createMemcachedClient();
        } catch (IOException e) {
            System.err.println("Couldn't create a connection to MemCachier: " +
                    e.getMessage());
        }

        if (mc == null) {
            System.err.println("mc is null. Exiting.");
            System.exit(1);
        }
        try {
            mc.set(key, 20, value);
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            System.err.println("Error:" + e.getMessage());
            e.printStackTrace();
        }

        try {
            assertTrue(value == mc.get(key));
        } catch (TimeoutException | InterruptedException | MemcachedException e) {
            System.err.println("Error:" + e.getMessage());
            e.printStackTrace();
        }

    }

    private static MemcachedClient createMemcachedClient() throws IOException {
        List<InetSocketAddress> servers = AddrUtil.getAddresses(System.getenv("MEMCACHIER_SERVERS").replace(",", " "));
        AuthInfo authInfo = AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"), System.getenv("MEMCACHIER_PASSWORD"));

        // this builds the memcache client
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);

        // Configure SASL auth for each server
        for (InetSocketAddress server : servers) {
            builder.addAuthInfo(server, authInfo);
        }

        // Use binary protocol
        builder.setCommandFactory(new BinaryCommandFactory());
        // Connection timeout in milliseconds (default: )
        builder.setConnectTimeout(1000);
        // Reconnect to servers (default: true)
        builder.setEnableHealSession(true);
        // Delay until reconnect attempt in milliseconds (default: 2000)
        builder.setHealSessionInterval(2000);
        MemcachedClient mc = builder.build();
        return mc;
    }
}
