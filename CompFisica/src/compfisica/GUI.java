package compfisica;

import Arduino.AcessaArduino;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI extends JFrame implements Observer {

    private final Container cp;
    private JPanel jPNorte = new JPanel();
    private JPanel jPCentro = new JPanel();
    private JPanel jPLeste = new JPanel();
    private JPanel jPSul = new JPanel();

    //Latitude e Longitude
    JLabel jLbLat = new JLabel("Latitude: ");
    JLabel jLbLon = new JLabel("Longitute: ");
    JTextField jTfLat = new JTextField(20);
    JTextField jTfLon = new JTextField(20);

    private javax.swing.JButton botaoIniciar = new javax.swing.JButton("Iniciar");

    //Mapa
    JLabel jLbMapa = new JLabel();
    private javax.swing.JButton botaoZoomMais = new javax.swing.JButton("+");
    private javax.swing.JButton botaoZoomMenos = new javax.swing.JButton("-");
    int zoom = 18;
    Double latitude = -24.025836, longitude = -52.368665;
    Mapa mapa = new Mapa();

    private javax.swing.JButton botaoFrente = new javax.swing.JButton("Frente");
    private javax.swing.JButton botaoAtras = new javax.swing.JButton("Atrás");
    private javax.swing.JButton botaoEsq = new javax.swing.JButton("Esquerda");
    private javax.swing.JButton botaoDir = new javax.swing.JButton("Direita");
    private javax.swing.JButton botaoParar = new javax.swing.JButton("Parar");
    private javax.swing.JButton botaoControl = new javax.swing.JButton("Controle");

    private ArrayList<String> caminho = new ArrayList<>();
    
    
    JLabel jLbReal = new JLabel("Tempo Real: ");
    JLabel jLbArmazena = new JLabel("Armazena dados: ");

    AcessaArduino acessaArduino;

    public GUI() throws IOException {

        try {
            acessaArduino = new AcessaArduino(this);
            System.out.println("porta detectada: " + acessaArduino.getPortaSelecionada());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao acionar arduino");
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(850, 700);
        setTitle("Projeto");
        setLocationRelativeTo(null);

        cp = getContentPane();
        cp.setLayout(new BorderLayout());

        jPNorte.setBackground(Color.lightGray);
        jPLeste.setBackground(Color.lightGray);
        jPCentro.setBackground(Color.lightGray);
        jPSul.setBackground(Color.lightGray);

        inicializaPainelNorte();
        inicializaPainelCentro();
        inicializaPainelLeste();
        inicializaPainelSul();

        cp.add(jPNorte, BorderLayout.NORTH);
        cp.add(jPLeste, BorderLayout.EAST);
        cp.add(jPCentro, BorderLayout.CENTER);
        cp.add(jPSul, BorderLayout.SOUTH);

        configuraBotao();

        setVisible(true); //deixar na última linha da classe

    }

    private void configuraBotao() {
        botaoControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acessaArduino.setDataToArduino(acessaArduino.getSerialPort(), "2");
                for (int i = 0; i < caminho.size(); i++) {
                    String c = caminho.get(i);
                    System.out.println(c);
                    acessaArduino.setDataToArduino(acessaArduino.getSerialPort(), c);
                }
                acessaArduino.setDataToArduino(acessaArduino.getSerialPort(), "0");
                caminho.clear();
            }
        });

        botaoIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                latitude = Double.valueOf(jTfLat.getText());
                longitude = Double.valueOf(jTfLon.getText());
                try {
                    jLbMapa.setIcon(mapa.atualizaMapa(zoom, String.valueOf(latitude), String.valueOf(longitude)));
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                acessaArduino.setDataToArduino(acessaArduino.getSerialPort(), "1");
                acessaArduino.setDataToArduino(acessaArduino.getSerialPort(), latitude.floatValue());
                acessaArduino.setDataToArduino(acessaArduino.getSerialPort(), longitude.floatValue());
            }
        });

        botaoZoomMenos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom--;
                try {
                    jLbMapa.setIcon(mapa.atualizaMapa(zoom, String.valueOf(latitude), String.valueOf(longitude)));
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        botaoZoomMais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoom++;
                try {
                    jLbMapa.setIcon(mapa.atualizaMapa(zoom, String.valueOf(latitude), String.valueOf(longitude)));
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        botaoFrente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caminho.add("f");
            }
        });

        botaoAtras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caminho.add("a");
            }
        });

        botaoEsq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caminho.add("e");
            }
        });

        botaoDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caminho.add("d");
            }
        });

        botaoParar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caminho.add("p");
            }
        });

    }

    private void inicializaPainelNorte() {
        GridBagLayout layoutNorte = new GridBagLayout();
        jPNorte.setLayout(layoutNorte);

        //Botão de zoom do mapa
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        jPNorte.add(botaoZoomMais, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        jPNorte.add(botaoZoomMenos, gbc);
    }

    private void inicializaPainelLeste() {
        GridBagLayout layoutleste = new GridBagLayout();
        jPLeste.setLayout(layoutleste);

        botaoFrente.setPreferredSize(new Dimension(101, 25));
        botaoAtras.setPreferredSize(new Dimension(101, 25));
        botaoEsq.setPreferredSize(new Dimension(101, 25));
        botaoDir.setPreferredSize(new Dimension(101, 25));
        botaoParar.setPreferredSize(new Dimension(101, 25));
        botaoControl.setPreferredSize(new Dimension(101, 25));
        botaoControl.setBackground(Color.blue);
        botaoControl.setForeground(Color.white);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        jPLeste.add(jLbReal, gbc);           //linha 0 coluna 1
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        jPLeste.add(botaoFrente, gbc);      //linha 1 coluna 1

        gbc.gridx = 1;
        gbc.gridy = 3;
        jPLeste.add(botaoAtras, gbc);      //linha 3 coluna 1

        gbc.gridx = 0;
        gbc.gridy = 2;
        jPLeste.add(botaoEsq, gbc);      //linha 2 coluna 0

        gbc.gridx = 2;
        gbc.gridy = 2;
        jPLeste.add(botaoDir, gbc);      //linha 2 coluna 0

        gbc.gridx = 1;
        gbc.gridy = 2;
        jPLeste.add(botaoParar, gbc);      //linha 2 coluna 1

        gbc.gridx = 0;
        gbc.gridy = 4;
        jPLeste.add(botaoControl, gbc);     //linha 4 coluna 0

    }

    private void inicializaPainelCentro() throws IOException {
        jPCentro.setLayout(new BoxLayout(jPCentro, BoxLayout.Y_AXIS));
        //Mapa
        jLbMapa.setIcon(mapa.CriaMapa(String.valueOf(latitude), String.valueOf(longitude), zoom));
        jLbMapa.validate();
        jPCentro.add(jLbMapa);
    }

    private void inicializaPainelSul() {
        GridBagLayout layoutSul = new GridBagLayout();
        jPSul.setLayout(layoutSul);
        
        botaoIniciar.setPreferredSize(new Dimension(100, 25));
        botaoIniciar.setBackground(Color.blue);
        botaoIniciar.setForeground(Color.white);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        jPSul.add(jLbLat, gbc);   // coluna 0, linha 0

        gbc.gridx = 1;
        gbc.gridy = 0;
        jPSul.add(jTfLat, gbc);   // coluna 1, linha 0.

        gbc.gridx = 0;
        gbc.gridy = 1;
        jPSul.add(jLbLon, gbc);   // coluna 0, linha 1.

        gbc.gridx = 1;
        gbc.gridy = 1;
        jPSul.add(jTfLon, gbc);   // coluna 1, linha 1.

        gbc.gridx = 0;
        gbc.gridy = 2;
        jPSul.add(botaoIniciar, gbc);   // coluna 0, linha 2.

    }

    @Override
    public void update(Observable o, Object arg) {
//        lbResposta.setText(arg.toString());
//
//        if (arg instanceof Integer) {
//            //foi setado um valor na janela observada....             
//            lbResposta.setText(String.valueOf(((Integer) arg).intValue())); //valor informado na thread
//
////            tf.setText(String.valueOf(contador));
//        } else if (arg instanceof Boolean) {
//            if (((Boolean) arg).booleanValue()) {
//            }
//        }
    }

//    public static void main(String[] args) throws IOException {
//        new GUI();
//    }
}
