package chc.fortumo.test;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProcessControllerTest {
    private String path = "fortumo/process/compute";
    private BasicCookieStore cookieStore;
    private HttpClient client;
    private HttpPost httpPost;

    @LocalServerPort
    private int port;

    @BeforeAll
    public void init() {
        // Init the required http client with fixed JSESIONID
        cookieStore = new BasicCookieStore();
        BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "1234");
        cookie.setDomain("localhost");
        cookie.setPath("/"+path);
        cookieStore.addCookie(cookie);

        client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

        httpPost = new HttpPost("http://localhost:" + this.port + "/" + path);
    }

    @AfterAll
    public void doneTesting() {
        // Once we are done, we close all the connection
        HttpClientUtils.closeQuietly(client);
    }

    @Test
    public void test0_Empty() throws Exception {
        HttpResponse response = client.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());
        assertThat(result).isEqualTo("0");
    }

    @Test
    public void test1_InvalidNumber() throws Exception {
        List<NameValuePair> urlParameters = new ArrayList<>();

        // number = aa
        urlParameters.add(new BasicNameValuePair("number", "abc"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());
        assertThat(result).isEqualTo("error");

        // number == 1
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "1"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        client.execute(httpPost);

        // number == 2
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "2"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        client.execute(httpPost);

        // number == 3
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "3"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        client.execute(httpPost);

        // number = end
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "end"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        response = client.execute(httpPost);
        result = EntityUtils.toString(response.getEntity());
        assertThat(result).isEqualTo("6");
    }

    @Test
    public void test2_SimpleHappyPath() throws Exception {
        List<NameValuePair> urlParameters = new ArrayList<>();

        // number = 1
        urlParameters.add(new BasicNameValuePair("number", "1"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        client.execute(httpPost);

        // number = 2
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "2"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        client.execute(httpPost);

        // number = end
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "end"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());
        assertThat(result).isEqualTo("3");

        // number = 100
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "100"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        client.execute(httpPost);

        // number = 2000
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "2000"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        client.execute(httpPost);

        // number = end
        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "end"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        response = client.execute(httpPost);
        result = EntityUtils.toString(response.getEntity());
        assertThat(result).isEqualTo("2100");
    }

    @Test
    public void test3_MultiThreadedHappyPath() throws Exception {
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("number", "10"));

        int threads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);

        for (int i = 1; i <= threads; i++) {
            Future futureTask = executorService.submit(() -> {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
                    client.execute(httpPost);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            futureTask.get();
        }

        urlParameters.clear();
        urlParameters.add(new BasicNameValuePair("number", "end"));
        httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        HttpResponse response = client.execute(httpPost);
        String result = EntityUtils.toString(response.getEntity());
        assertThat(result).isEqualTo("1000");

        executorService.shutdown();
    }
}
