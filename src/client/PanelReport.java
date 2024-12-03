package client;

<<<<<<< HEAD
=======
import java.awt.Color;
>>>>>>> finalcheck1
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.*;

import objects.*;

public class PanelReport extends PanelBase {
    final private MainFrame frame;
    final private ObjectOutputStream ostream;
    final private ObjectInputStream istream;

    public PanelReport(MainFrame frame, ObjectOutputStream ostream, ObjectInputStream istream) {
        this.frame = frame;
        this.ostream = ostream;
        this.istream = istream;
    }

    SwingWorker<ServerMsg, Void> getReportWorker() {
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg, Void>() {
            @Override
            protected ServerMsg doInBackground() throws Exception {
                ostream.writeObject(new ClientMsg("GET", "report", null));
                return (ServerMsg) istream.readObject();
            }

            @Override
            protected void done() {
                try {
                    var resp = get(3, TimeUnit.SECONDS);
                    if (resp.isOk()) {
                        var body = (BodyReport) resp.getBody();
                        String content = body.toString();
                        JTextArea reportDump = new JTextArea(content);
                        reportDump.setEditable(false);
                        add(reportDump);
                        JButton downloadBtn = new JButton("Save report");
                        downloadBtn.setBackground(new Color(146, 140, 237));
                        downloadBtn.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                JFileChooser fileChooser = new JFileChooser();
                                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                                    File file = fileChooser.getSelectedFile();
                                    try (FileWriter writer = new FileWriter(file)) {
                                        writer.write(content);
                                    } catch (IOException err) {
                                        JOptionPane.showMessageDialog(null, "Can't write to file.");
                                    }
                                }
                            };
                        });
                        add(downloadBtn);
                        refreshPanel();
                    }
                } catch (TimeoutException err) {
                    frame.showTimeoutDialog();
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(null, err.getMessage());
                }
            }
        };
        return worker;
    }

    @Override
    void onLoad() {
        var worker = getReportWorker();
        worker.execute();
    }

    @Override
    void onUnload() {
        removeAll();
    }
}
