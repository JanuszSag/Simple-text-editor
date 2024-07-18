import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

public class Main extends JFrame{
    private static JTextArea jta = new JTextArea();
    JFileChooser jfc;
    boolean saved = false;
    public enum Clr{GREEN,ORANGE,RED,BLACK,WHITE,YELLOW,BLUE}
    public enum ClrName{Green,Orange,Red,Black,White,Yellow,Blue}
    JFrame jf = new JFrame();
    JLabel pFg = new JLabel("fg");
    JLabel pBg = new JLabel("bg");
    JLabel pFs = new JLabel("");
    JLabel state = new JLabel("new");
    String path = "";


    public static void main(String[] args) {new Main();}

    public Main(){
        String name = "bez tytułu";
        openHandler oHangler = new openHandler();
        saveHandler sHandler = new saveHandler();
        saveAsHandler saHandler = new saveAsHandler();
        jf.setTitle("Prosty edytor - "+name);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setPreferredSize(new Dimension(600,600));

        JPanel total = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new BorderLayout());
        JMenuBar menuBar = new JMenuBar();
        //============================File
        JMenu menu = new JMenu("File");
        JMenuItem Open = new JMenuItem("Open");
        KeyStroke openStroke = KeyStroke.getKeyStroke("control O");
        Open.setAccelerator(openStroke);
        Open.addActionListener(oHangler);
        JMenuItem Save = new JMenuItem("Save");
        KeyStroke saveStroke = KeyStroke.getKeyStroke("control S");
        Save.setAccelerator(saveStroke);
        Save.addActionListener(sHandler);
        JMenuItem SaveAs = new JMenuItem("Save As");
        KeyStroke saveAsStroke = KeyStroke.getKeyStroke("control alt S");
        SaveAs.setAccelerator(saveAsStroke);
        SaveAs.addActionListener(saHandler);
        JMenuItem Exit = new JMenuItem("Exit");
        Exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
            }
        });
        KeyStroke ExitStroke = KeyStroke.getKeyStroke("control X");
        Exit.setAccelerator(ExitStroke);
        JSeparator JSep = new JSeparator();
        JSep.setBackground(Color.RED);
        menu.add(Open);
        menu.add(Save);
        menu.add(SaveAs);
        menu.add(JSep);
        menu.add(Exit);
        menuBar.add(menu);
        //============================Edit
        JMenu Edit = new JMenu("Edit");
        JMenuItem Praca = new JMenuItem("Praca");
        KeyStroke PracaStroke = KeyStroke.getKeyStroke("control shift P");
        Praca.setAccelerator(PracaStroke);
        Praca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jta.insert("Wieliszew ul.Grzybowa 42",jta.getSelectionStart());
            }
        });
        JMenuItem Dom = new JMenuItem("Dom");
        KeyStroke DomStroke = KeyStroke.getKeyStroke("control shift D");
        Dom.setAccelerator(DomStroke);
        Dom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jta.insert("Modlin ul.Mieszka I 102",jta.getSelectionStart());
            }
        });
        JMenuItem Szkola = new JMenuItem("Szkoła");
        KeyStroke SzkolaStroke = KeyStroke.getKeyStroke("control shift S");
        Szkola.setAccelerator(SzkolaStroke);
        Szkola.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jta.insert("Warszawa ul.Koszykowa 86",jta.getSelectionStart());
            }
        });
        Edit.add(Praca);
        Edit.add(new JSeparator());
        Edit.add(Dom);
        Edit.add(new JSeparator());
        Edit.add(Szkola);
        menuBar.add(Edit);
        //============================Options
        JMenu Options = new JMenu("Options");
        //========================================submenu Foreground
        JMenu Foreground = new JMenu("Foreground");
        ButtonGroup fgButton = new ButtonGroup();
        JRadioButtonMenuItem[] fg = new JRadioButtonMenuItem[7];
        for (int i = 0; i < Clr.values().length; i++) {
            fg[i] = new JRadioButtonMenuItem(ClrName.values()[i].toString());

            try {
                Field f = Color.class.getField(Clr.values()[i].name());
                fg[i].setForeground((Color) f.get(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            fg[i].setIcon(new RysujKolka(fg[i].getForeground(),10,10));
            fg[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JMenuItem tmp = (JMenuItem) e.getSource();
                    jta.setForeground(tmp.getForeground());
                    paint(jf.getGraphics());
                    pFg.setText(" fg");
                    pFg.setIcon(new RysujKolka(tmp.getForeground(),10,10));
                }
            });
            fgButton.add(fg[i]);
            Foreground.add(fg[i]);
        }
        Options.add(Foreground);
        //========================================submenu Background
        JMenu Background = new JMenu("Background");
        ButtonGroup bgButton = new ButtonGroup();
        JRadioButtonMenuItem[] bg = new JRadioButtonMenuItem[7];
        for (int i = 0; i < Clr.values().length; i++) {
            bg[i] = new JRadioButtonMenuItem(ClrName.values()[i].toString());
            try {
                Field f = Color.class.getField(Clr.values()[i].name());
                bg[i].setForeground((Color) f.get(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            bg[i].setIcon(new RysujKolka(bg[i].getForeground(),10,10));
            bg[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JMenuItem tmp = (JMenuItem) e.getSource();
                    jta.setBackground(tmp.getForeground());
                    pBg.setText(tmp.getText()+" bg");
                    pFg.setIcon(new RysujKolka(tmp.getForeground(),10,10));
                }
            });
            bgButton.add(bg[i]);
            Background.add(bg[i]);
        }
        Options.add(Background);
        //=======================================submenu Font size
        JMenu size = new JMenu("Font size");
        JMenuItem[] fs = new JMenuItem[9];
        int index = 0;
        for(int i=8;i<=24;i+=2){
            fs[index] = new JMenuItem(i+" pts");
            fs[index].setFont(new Font("Serif",Font.PLAIN,i));
            fs[index].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JMenuItem tmp = (JMenuItem) e.getSource();
                    StringBuilder sb = new StringBuilder();
                    sb.append(tmp.getText().charAt(0));
                    if(tmp.getText().charAt(1)!=' ')
                        sb.append(tmp.getText().charAt(1));
                    int val = Integer.parseInt(sb.toString());
                    jta.setFont(new Font("Serif",Font.PLAIN,val));
                    pFs.setText(" "+sb);
                }
            });
            size.add(fs[index++]);
        }


        Options.add(size);
        //=============================Finishing top
        menuBar.add(Options);
        top.add(menuBar);
        total.add(top,BorderLayout.NORTH);
        //=============================Creating JTextArea with scrolls and adding it to total
        jta.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                state.setText("Modified");
            }

            @Override
            public void removeUpdate(DocumentEvent e) {

                state.setText("Modified");
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                state.setText("Modified");
            }
        });
        JScrollPane scroll = new JScrollPane(jta);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        total.add(scroll,BorderLayout.CENTER);
        //============================Bot
        JPanel bot = new JPanel(new BorderLayout());
        JPanel botLeft = new JPanel(new GridLayout(1,3));
        botLeft.add(pFg);
        botLeft.add(pBg);
        botLeft.add(pFs);
        bot.add(botLeft,BorderLayout.WEST);
        bot.add(state,BorderLayout.EAST);
        total.add(bot,BorderLayout.SOUTH);
        //===========================adding everything to JFrame
        jf.add(total);
        jf.pack();
        jf.setVisible(true);
    }
    public class openHandler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(path=="")
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            else jfc = new JFileChooser(path);
            int returnValue = jfc.showOpenDialog(jf);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                saved=true;
                jf.setTitle("Prosty edytor - "+selectedFile.getAbsolutePath());
                path=selectedFile.getAbsolutePath();
                try {
                    jta.setText("");
                    Scanner myReader = new Scanner(selectedFile);
                    while(myReader.hasNextLine()){
                        String linijka = myReader.nextLine();
                        jta.append(linijka+"\n");
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public class saveAsHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(path=="")
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            else jfc = new JFileChooser(path);
            int returnValue = jfc.showSaveDialog(jfc.getParent());
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = jfc.getSelectedFile();
                jf.setTitle("Prosty edytor - "+selectedFile.getAbsolutePath());
                path=selectedFile.getAbsolutePath();
                saved=true;
                try {
                    PrintWriter writer = new PrintWriter(selectedFile.getAbsolutePath());
                    writer.print(jta.getText());
                    writer.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            state.setText("saved");
        }
    }
    public class saveHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!saved) {
                jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnValue = jfc.showSaveDialog(jfc.getParent());
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    jf.setTitle("Prosty edytor - " + selectedFile.getAbsolutePath());
                    jfc.setCurrentDirectory(jfc.getSelectedFile());
                    try {
                        PrintWriter writer = new PrintWriter(selectedFile.getAbsolutePath());
                        writer.print("");
                        writer.print(jta.getText());
                        writer.close();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            else{
                File selectedFile = jfc.getSelectedFile();
                jf.setTitle("Prosty edytor - " + selectedFile.getAbsolutePath());
                try {
                    PrintWriter writer = new PrintWriter(selectedFile.getAbsolutePath());
                    writer.print("");
                    writer.print(jta.getText());
                    writer.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
            state.setText("saved");
        }

    }
    class RysujKolka implements Icon{
        private Color kolor;
        private int height;
        private int width;

        public RysujKolka(Color kolor, int height, int width) {
            this.kolor = kolor;
            this.height = height;
            this.width = width;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(kolor);
            g2d.fillOval(x,y,width,height);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }


}
