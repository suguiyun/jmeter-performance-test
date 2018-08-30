package com.cryptoex;

import com.cryptoex.response.BusinessOrder;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Map;

public class JmeterPerformanceTest extends AbstractJavaSamplerClient {
    private String inNum;
    private int inNumInt;
    private Client client;

    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        // 添加一个新参数
        params.addArgument("inNum", "");
        return params;
    }

    public void setupTest(JavaSamplerContext jsc) {
        inNum = jsc.getParameter("inNum", "");
        inNumInt = Integer.parseInt(inNum);
    }

    public SampleResult runTest(JavaSamplerContext arg0) {
        JsonKey.Group group = (JsonKey.Group) JsonKey.list.get(Integer.valueOf(inNum)-1);
        client = new Client(group.apiKey, group.apiSecret);
        SampleResult results = new SampleResult();
        try {
            results.sampleStart();

            Map<String, Object> orders;
            if (inNumInt % 2 == 0) {
                orders = client.request(Map.class, null, "POST", "/v1/trade/orders", null, new BusinessOrder("1", "BUY_LIMIT", "1.2", "BTC_USDT"), null);
            } else {
                orders = client.request(Map.class, null, "POST", "/v1/trade/orders", null, new BusinessOrder("1", "SELL_LIMIT", "1.2", "BTC_USDT"), null);
            }
            results.setResponseData(orders.toString().getBytes());

            results.setResponseData(inNum.getBytes());
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
