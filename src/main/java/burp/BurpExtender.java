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
        // �������
        this.callbacks = iBurpExtenderCallbacks;
        callbacks.setExtensionName("DNS Cache 0.1");

        // �������ʱ��ʾ������
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stdout.println("[+] DNS Cache Start~");
        this.stdout.println("[+] ^_^");
        this.stdout.println("[+]");
        this.stdout.println("[+]#####################################");
        this.stdout.println("[+]    DNS Cache  v0.1");
        this.stdout.println("[+]    anthor: mayi077");
        this.stdout.println("[+]    github: https://github.com/mayi077/BurpDnsCache");
        this.stdout.println("[+]#####################################");

        // ���burp��ǩ
        callbacks.addSuiteTab(this);

    }

    @Override
    public String getTabCaption() {
        // ���ò����
        return "DNS Cache";
    }

    @Override
    public Component getUiComponent() {
        if (jPanelMain == null) {
            jPanelMain = new JPanel();
            jPanelMain.setLayout(new BorderLayout());
            // �����ı������ñ߿�
            textField = new JTextArea(45, 40);
            textField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // ���ñ߿�
            // ���ù�����
            JScrollPane jScrollPane = new JScrollPane(textField);
            // ����ı������
            jPanelMain.add(jScrollPane, BorderLayout.NORTH);

            JButton submitButton = new JButton("Submit"); // �����ύ��ť
            JButton resetButton = new JButton("Reset"); // �������ð�ť
            JButton editButton = new JButton("Edit"); // �����༭��ť

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.add(submitButton); // ���ύ��ť��ӵ������
            buttonPanel.add(editButton); // ���༭��ť��ӵ������
            buttonPanel.add(resetButton); // �����ð�ť��ӵ������
            jPanelMain.add(buttonPanel);

            // ����ύ��ť���¼�������
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inputString = textField.getText();
                    // �����ַ�����������
                    parsedString = parseInputString(inputString);
                    // �������ýӿ�
                    stdout.println("logs:\n " + parsedString);
                    callbacks.loadConfigFromJson(parsedString);
                    // �����ı�����ύ��ť�������ñ༭��ť
                    textField.setEditable(false);
                    submitButton.setEnabled(false);
                    editButton.setEnabled(true);
                }
            });
            // ������ð�ť���¼�������
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    textField.setText(""); // �� JTextArea ���ı���������Ϊ���ַ���
                    // �����ı�����ύ��ť�������ñ༭��ť
                    textField.setEditable(true);
                    submitButton.setEnabled(true);
                    editButton.setEnabled(false);
                }
            });

            // ��ӱ༭��ť���¼�������
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // �����ı�����ύ��ť�������ñ༭��ť
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
        // ���������ַ�������ÿһ�зָ��ip��ַ�������б�
        String[] lines = inputString.split("\\r?\\n");
        List<Host> hosts = new ArrayList<>();
        for (String line : lines) {
            // ���Կ��к�ע����
            if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
            String[] parts = line.trim().split("\\s+");
            Host host = new Host();
            host.ipAddress = parts[0];
            host.hostnames = new ArrayList<>();
            for (int i = 1; i < parts.length; i++) {
                // �ж�����ע��
                if (parts[i].startsWith("#")) {
                    break;
                }
                host.hostnames.add(parts[i]);
            }
            hosts.add(host);
        }
        // ʹ���ַ���ƴ�ӵķ�ʽ����JSON�ַ��������������̨
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