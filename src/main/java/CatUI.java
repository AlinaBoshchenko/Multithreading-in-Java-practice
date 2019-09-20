import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CatUI extends JFrame {

    private CustomJPanel p;

    public CatUI() {
        redirectSystemStreams();
        gui();
    }

    //setting the frame of Cats
    public void gui() {

        setTitle("Cats Box");
        setVisible(true);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//creating panel and adding to Frame
        p = new CustomJPanel();
        p.setBackground(Color.GRAY);
        getContentPane().add(p);
    }

    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                p.addMessage(String.valueOf((char) b));
                p.repaint();
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                p.addMessage(new String(b, off, len));
                p.repaint();

            }

            @Override
            public void write(byte[] b) throws IOException {
                p.addMessage(new String(b, 0, b.length));
                p.repaint();
            }

        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));


    }


}

class CustomJPanel extends JPanel {

    List<String> messages = new ArrayList<>();

    public void addMessage(String s) {
        messages.add(s);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.CYAN);

        int i = 1;
        for (String mess : messages) {
            g.drawString(mess, 20, i * 20);
            i++;
        }
    }


}
