package com.cryptoex;

import com.cryptoex.response.BusinessOrder;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Map;

public class JmeterPerformanceTest extends AbstractJavaSamplerClient {
    private int threadId;
    private Client client;

    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        // 添加一个新参数
        params.addArgument("threadId", "");
        return params;
    }

    public void setupTest(JavaSamplerContext jsc) {
        threadId = Integer.valueOf(jsc.getParameter("threadId", ""));
        JsonKey.Group group = JsonKey.list.get(threadId-1);
        client = new Client(group.apiKey, group.apiSecret);
    }

    public SampleResult runTest(JavaSamplerContext arg0) {
        SampleResult results = new SampleResult();
        try {
            results.sampleStart();
            Map<String, Object> orders;
            if (threadId % 2 == 0) {
                orders = client.request(Map.class, null, "POST", "/v1/trade/orders", null, new BusinessOrder("0.1", "BUY_LIMIT", "1", "BTC_USDT"), null);
            } else {
                orders = client.request(Map.class, null, "POST", "/v1/trade/orders", null, new BusinessOrder("0.1", "SELL_LIMIT", "1", "BTC_USDT"), null);
            }
            results.setResponseData(orders.toString().getBytes());
            results.setSuccessful(true);
        } catch (Exception e) {
            results.setResponseData(e.getMessage().getBytes());
            results.setSuccessful(false);
        } finally {
            results.sampleEnd();
        }
        return results;
    }

    public void teardownTest(JavaSamplerContext arg0) {
    }
}
