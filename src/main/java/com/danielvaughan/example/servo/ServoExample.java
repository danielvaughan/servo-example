package com.danielvaughan.example.servo;

import com.netflix.servo.publish.*;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.TagList;
import com.sun.net.httpserver.HttpServer;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServoExample {

    private ServoExample() {
    }

    private static MetricObserver rateTransform(MetricObserver observer) {
        final long heartbeat = 2 * Config.getPollInterval();
        return new CounterToRateMetricTransform(observer, heartbeat, TimeUnit.SECONDS);
    }

    private static MetricObserver async(String name, MetricObserver observer) {
        final long expireTime = 2000 * Config.getPollInterval();
        final int queueSize = 10;
        return new AsyncMetricObserver(name, observer, queueSize, expireTime);
    }

    private static void schedule(MetricPoller poller, List<MetricObserver> observers) {
        final PollRunnable task = new PollRunnable(poller, BasicMetricFilter.MATCH_ALL,
                true, observers);
        PollScheduler.getInstance().addPoller(task, Config.getPollInterval(), TimeUnit.SECONDS);
    }

    private static void initMetricsPublishing() throws Exception {
        final List<MetricObserver> observers = new ArrayList<>();

        PollScheduler.getInstance().start();
        schedule(new MonitorRegistryMetricPoller(), observers);

        if (Config.isJvmPollerEnabled()) {
            schedule(new JvmMetricPoller(), observers);
        }
    }

    private static void initHttpServer() throws Exception {
        // Setup default endpoints
        final HttpServer server = HttpServer.create();
        server.createContext("/echo", new EchoHandler());

        // Hook to allow for graceful exit
        final Closeable c = () -> {
            PollScheduler.getInstance().stop();
            server.stop(5);
        };
        server.createContext("/exit", new ExitHandler(c));

        // Bind and start server
        server.bind(new InetSocketAddress(Config.getPort()), 0);
        server.start();
    }

    public static void main(String[] args) throws Exception {
        initMetricsPublishing();
        initHttpServer();
    }
}
