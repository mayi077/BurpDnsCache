package burp;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class BurpExtender implements IBurpExtender, ITab {
    private IBurpExtenderCallbacks callbacks;
    private PrintWriter stdout;
    private JPanel jPanelMain;
    private JTextArea textField;
    private String inputString;
    private String parsedString;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks iBurpExtenderCallbacks) {
        // 插件名称
        this.callbacks = iBurpExtenderCallbacks;
        callbacks.setExtensionName("DNS Cache 0.1");

        // 插件激活时显示的内容
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stdout.println("[+] DNS Cache Start~");
        this.stdout.println("[+] ^_^");
        this.stdout.println("[+]");
        this.stdout.println("[+]#####################################");
        this.stdout.println("[+]    DNS Cache  v0.1");
        this.stdout.println("[+]    anthor: mayi077");
        this.stdout.println("[+]    github: https://github.com/mayi077/BurpDnsCache");
        this.stdout.println("[+]#####################################");

        // 添加burp标签
        callbacks.addSuiteTab(this);

    }

    @Override
    public String getTabCaption() {
        // 设置插件名
        return "DNS Cache";
    }

    @Override
    public Component getUiComponent() {
        if (jPanelMain == null) {
            jPanelMain = new JPanel();
            jPanelMain.setLayout(new BorderLayout());
            // 创建文本框并设置边框
            textField = new JTextArea(45, 40);
            textField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 设置边框
            // 设置滚动条
            JScrollPane jScrollPane = new JScrollPane(textField);
            // 添加文本框到面板
            jPanelMain.add(jScrollPane, BorderLayout.NORTH);

            JButton submitButton = new JButton("Submit"); // 创建提交按钮
            JButton resetButton = new JButton("Reset"); // 创建重置按钮
            JButton editButton = new JButton("Edit"); // 创建编辑按钮

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(submitButton); // 将提交按钮添加到面板中
            buttonPanel.add(editButton); // 将编辑按钮添加到面板中
            buttonPanel.add(resetButton); // 将重置按钮添加到面板中
            jPanelMain.add(buttonPanel);

            // 添加提交按钮的事件监听器
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inputString = textField.getText();
                    // 调用字符串解析函数
                    parsedString = parseInputString(inputString);
                    // 调用配置接口
                    stdout.println("logs:\n " + parsedString);
                    callbacks.loadConfigFromJson(parsedString);
                    // 禁用文本框和提交按钮，并启用编辑按钮
                    textField.setEditable(false);
                    submitButton.setEnabled(false);
                    editButton.setEnabled(true);
                }
            });
            // 添加重置按钮的事件监听器
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textField.setText(""); // 将 JTextArea 的文本内容设置为空字符串
                    // 启用文本框和提交按钮，并禁用编辑按钮
                    textField.setEditable(true);
                    submitButton.setEnabled(true);
                    editButton.setEnabled(false);
                }
            });

            // 添加编辑按钮的事件监听器
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 启用文本框和提交按钮，并禁用编辑按钮
                    textField.setEditable(true);
                    submitButton.setEnabled(true);
                    editButton.setEnabled(false);
                }
            });

        }

        return jPanelMain;
    }

    private static class Host {
        String ipAddress;
        List<String> hostnames;
    }

    private String parseInputString(String inputString) {
        // 解析输入字符串，将每一行分割成ip地址和主机列表
        String[] lines = inputString.split("\\r?\\n");
        List<Host> hosts = new ArrayList<>();
        for (String line : lines) {
            // 忽略空行和注释行
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
            String[] parts = line.trim().split("\\s+");
            Host host = new Host();
            host.ipAddress = parts[0];
            host.hostnames = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                // 判断行内注释
                if (parts[i].startsWith("#")) {
                    break;
                }
                host.hostnames.add(parts[i]);
            }
            hosts.add(host);
        }
        // 使用字符串拼接的方式生成JSON字符串并输出到控制台
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("    \"project_options\": {\n");
        sb.append("        \"connections\": {\n");
        sb.append("            \"hostname_resolution\": [\n");
        for (int i = 0; i < hosts.size(); i++) {
            Host host = hosts.get(i);
            for (String hostname : host.hostnames) {
                sb.append("                {\n");
                sb.append("                    \"enabled\": true,\n");
                sb.append("                    \"hostname\": \"" + hostname + "\",\n");
                sb.append("                    \"ip_address\": \"" + host.ipAddress + "\"\n");
                sb.append("                }");
                if (i != hosts.size() - 1
                        || !hostname.equals(host.hostnames.get(host.hostnames.size() - 1))) {
                    sb.append(",");
                }
                sb.append("\n");
            }
        }

        sb.append("            ]\n");
        sb.append("        }\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();

    }

}