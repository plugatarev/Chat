import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SwingView extends JFrame{
    private final JTextArea log = new JTextArea();
    private final JTextField name = new JTextField();
    private final JTextField fieldInput = new JTextField("dsf");
        public SwingView(){
            super("Chat");
            addReactionWindowClosing();
            this.setLayout(null);
            this.setPreferredSize(new Dimension(400, 600));
//            setupMenu();
            this.pack();
            this.setLocationRelativeTo(null);
//            fieldPanel.setFocusable(true);
            this.add(log, BorderLayout.CENTER);
            this.add(fieldInput, BorderLayout.SOUTH);
            this.add(name, BorderLayout.NORTH);
            this.setVisible(true);
        }

        public void update() {

        }

        private void addReactionWindowClosing(){
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        }

//        public void end() {
//            int score = fieldPanel.field.currentScore();
//            if (score > 0) {
//                String res;
//                do{
//                    res = JOptionPane.showInputDialog(this, "Enter your name");
//                }while(!isCorrect(res));
//                records.addNewScore(res, score);
//            }
//            showChoiceMenu();
//        }

        private boolean isCorrect(String res){
            if (res.length() == 0) return false;
            int i = 0;
            while (i < res.length()){
                if (res.charAt(i) == ' ') return false;
                i++;
            }
            return true;
        }


}
