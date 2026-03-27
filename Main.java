import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.sound.sampled.*;
import java.io.File;

public class Main {

    private static ArrayList<Pet> myPets = new ArrayList<>();
    private static JPanel petsPanel;
    private static Image backgroundImage;
    private static Map<String, ImageIcon> petIcons = new HashMap<>();

    private static Clip bgmClip;
    private static boolean isMuted = false;
    private static boolean isAutoFeed = false;

    public static void main(String[] args) {
        showStartMenu();
    }

    private static void showStartMenu() {
        JFrame menu = new JFrame("PET GAME");
        menu.setSize(400, 300);
        menu.setLayout(new GridLayout(3,1));

        JButton start = new JButton("START GAME");
        JButton exit = new JButton("EXIT");

        menu.add(new JLabel("🐾 PET GAME", SwingConstants.CENTER));
        menu.add(start);
        menu.add(exit);

        start.addActionListener(e -> {
            menu.dispose();
            startGame();
        });

        exit.addActionListener(e -> System.exit(0));

        menu.setVisible(true);
    }

    private static void startGame() {

        backgroundImage = new ImageIcon("bg.png").getImage();
        petIcons.put("dog.png", new ImageIcon("dog.png"));
        petIcons.put("cat.png", new ImageIcon("cat.png"));

        playBGM("bgm.wav");

        JFrame frame = new JFrame("Pet Game");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 🖼 พื้นหลัง
        petsPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        petsPanel.setLayout(null);

        // 🔝 TOP
        JPanel top = new JPanel();
        JButton muteBtn = new JButton("🔊");
        JButton autoBtn = new JButton("AUTO: OFF");

        top.add(muteBtn);
        top.add(autoBtn);

        // 🔽 BOTTOM
        JPanel bottom = new JPanel();
        JButton dogBtn = new JButton("ADD DOG");
        JButton catBtn = new JButton("ADD CAT");

        bottom.add(dogBtn);
        bottom.add(catBtn);

        frame.add(top, BorderLayout.NORTH);
        frame.add(petsPanel, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        // 🔊 mute
        muteBtn.addActionListener(e -> {
            isMuted = !isMuted;
            if (bgmClip != null) {
                if (isMuted) bgmClip.stop();
                else bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            muteBtn.setText(isMuted ? "🔇" : "🔊");
        });

        // 🤖 auto feed
        autoBtn.addActionListener(e -> {
            isAutoFeed = !isAutoFeed;
            autoBtn.setText(isAutoFeed ? "AUTO: ON" : "AUTO: OFF");
        });

        // 🐶
        dogBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Dog name:");
            if(name != null && !name.isEmpty()){
                Pet p = new Dog(name);
                myPets.add(p);
                playSound("dog.wav");
                addPet(p);
            }
        });

        // 🐱
        catBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Cat name:");
            if(name != null && !name.isEmpty()){
                Pet p = new Cat(name);
                myPets.add(p);
                playSound("cat.wav");
                addPet(p);
            }
        });

        frame.setVisible(true);
    }

    // ❤️
    private static String getHearts(int e) {
        int h = e / 20;
        String s = "";
        for(int i=0;i<h;i++) s += "❤️";
        return s;
    }

    // 🐾 ADD PET + AI + DEATH SYSTEM
    private static void addPet(Pet p){

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setSize(120,150);
        card.setOpaque(false);

        JLabel img = new JLabel(new ImageIcon(
                petIcons.get(p.getImagePath()).getImage()
                        .getScaledInstance(80,80,Image.SCALE_SMOOTH)
        ));

        String emoji = (p instanceof Dog) ? "🐶 " : "🐱 ";
        JLabel name = new JLabel(emoji + p.getName(), SwingConstants.CENTER);

        JLabel hearts = new JLabel(getHearts(p.getEnergy()), SwingConstants.CENTER);

        img.setAlignmentX(Component.CENTER_ALIGNMENT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        hearts.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(img);
        card.add(name);
        card.add(hearts);

        card.setLocation((int)(Math.random()*600), (int)(Math.random()*350));

        // 🎯 คลิกเล่น
        img.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent e){
                p.play();

                if(p instanceof Dog){
                    playSound("dog.wav");
                    JOptionPane.showMessageDialog(null, "Playing with dog 🐶");
                }
                if(p instanceof Cat){
                    playSound("cat.wav");
                    JOptionPane.showMessageDialog(null, "Playing with cat 🐱");
                }
            }
        });

        petsPanel.add(card);
        petsPanel.repaint();

        // 🐾 เดิน
        new javax.swing.Timer(1000, e -> {
            int x = card.getX() + (int)(Math.random()*40-20);
            int y = card.getY() + (int)(Math.random()*40-20);

            x = Math.max(0, Math.min(x, petsPanel.getWidth()-120));
            y = Math.max(0, Math.min(y, petsPanel.getHeight()-150));

            card.setLocation(x,y);
        }).start();

        // 💀 SYSTEM ตาย
        new javax.swing.Timer(1500, e -> {

            p.decreaseEnergy();

            if (isAutoFeed && p.getEnergy() <= 40) {
                p.feed();
            }

            if (p.getEnergy() <= 0) {

                playSound("die.wav");

                JOptionPane.showMessageDialog(null, p.getName() + " died 💀");

                // 🪦 หลุมศพ
                card.removeAll();

                JLabel grave = new JLabel(new ImageIcon(
                        new ImageIcon("grave.png").getImage()
                                .getScaledInstance(80,80,Image.SCALE_SMOOTH)
                ));

                JLabel rip = new JLabel("RIP " + p.getName(), SwingConstants.CENTER);
                rip.setForeground(Color.RED);

                grave.setAlignmentX(Component.CENTER_ALIGNMENT);
                rip.setAlignmentX(Component.CENTER_ALIGNMENT);

                card.add(grave);
                card.add(rip);

                petsPanel.repaint();

                // ⏳ ลบออก
                new javax.swing.Timer(2000, ev -> {
                    petsPanel.remove(card);
                    myPets.remove(p);
                    petsPanel.repaint();
                }).start();

                ((javax.swing.Timer)e.getSource()).stop();
                return;
            }

            hearts.setText(getHearts(p.getEnergy()));

        }).start();
    }

    // 🔊
    public static void playSound(String f){
        try{
            AudioInputStream a = AudioSystem.getAudioInputStream(new File(f));
            Clip c = AudioSystem.getClip();
            c.open(a);
            c.start();
        }catch(Exception e){}
    }

    // 🎵
    public static void playBGM(String f){
        try{
            AudioInputStream a = AudioSystem.getAudioInputStream(new File(f));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(a);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
        }catch(Exception e){}
    }
}
