import javax.swing.*;
import java.awt.*;

public class BufferOverflowLauncher {
    
    public static void main(String[] args) {
        try {
            // Set look and feel to system style
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Afficher un splash screen
        JWindow splashScreen = createSplashScreen();
        splashScreen.setVisible(true);
        
        // Lancer l'application principale après un court délai
        Timer timer = new Timer(2000, e -> {
            splashScreen.dispose();
            SwingUtilities.invokeLater(() -> {
                BufferOverflowDetector app = new BufferOverflowDetector();
                app.setVisible(true);
            });
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    private static JWindow createSplashScreen() {
        JWindow window = new JWindow();
        window.setSize(500, 300);
        window.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(new Color(64, 64, 64), 1));
        
        // Titre
        JLabel title = new JLabel("Détecteur de Dépassement de Tampon", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(50, 50, 150));
        
        // Sous-titre
        JLabel subtitle = new JLabel("TP EPS M1 IA - Université de Jijel", JLabel.CENTER);
        subtitle.setFont(new Font("Arial", Font.ITALIC, 16));
        
        // Logo (simulé avec un panneau coloré)
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Fond dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(100, 100, 255), 
                    getWidth(), getHeight(), new Color(200, 200, 255));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
                
                // Dessiner un symbole de "buffer overflow"
                g2d.setColor(new Color(50, 50, 150));
                g2d.setStroke(new BasicStroke(3f));
                
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                int boxSize = 60;
                
                // Dessiner un "buffer"
                g2d.drawRect(centerX - boxSize, centerY - boxSize/2, boxSize*2, boxSize);
                
                // Dessiner une flèche "overflow"
                g2d.setColor(new Color(220, 50, 50));
                g2d.drawLine(centerX + boxSize, centerY, centerX + boxSize + 40, centerY);
                g2d.fillPolygon(
                    new int[] {centerX + boxSize + 30, centerX + boxSize + 40, centerX + boxSize + 30},
                    new int[] {centerY - 10, centerY, centerY + 10},
                    3
                );
                
                g2d.dispose();
            }
        };
        logoPanel.setPreferredSize(new Dimension(200, 150));
        
        // Barre de progression
        JProgressBar progress = new JProgressBar(0, 100);
        progress.setIndeterminate(true);
        progress.setString("Chargement de l'application...");
        progress.setStringPainted(true);
        
        // Assemblage
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(logoPanel, BorderLayout.CENTER);
        topPanel.add(subtitle, BorderLayout.SOUTH);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(topPanel, BorderLayout.CENTER);
        panel.add(progress, BorderLayout.SOUTH);
        
        window.add(panel);
        return window;
    }
}