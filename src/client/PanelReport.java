package client;

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
        SwingWorker<ServerMsg, Void> worker = new SwingWorker<ServerMsg,Void>() {
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
                        System.out.println(body.toString());
                        JTextArea a = new JTextArea(body.toString());
                        add(a);
                        refreshPanel();
                    }
                }
                catch (TimeoutException err) {

                }
                catch (Exception err) {

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
