package game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Created by howard on 2018/2/8.
 */
class GameFrame extends JFrame implements ActionListener {
    private static GameFrame frame;
    private JPanel backPanel;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JButton btnOk;
    private JButton btnNextGeneration;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnExit;
    private JButton[][] btnBlock;
    private JLabel lblRow;
    private JLabel lblCol;
    private JComboBox rowList;
    private JComboBox colList;
    private boolean[][] isSelected;
    private int maxRow;
    private int maxCol;
    private Life life;
    private boolean isRunning;
    private Thread thread;
    private boolean isDead;

    public GameFrame() {
    }

    public static void main(String[] arg) {
        frame = new GameFrame("������ϷV1.0");
    }

    public int getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(int maxRow) {
        this.maxRow = maxRow;
    }

    public int getMaxCol() {
        return maxCol;
    }

    public void setMaxCol(int maxCol) {
        this.maxCol = maxCol;
    }

    public void initGui() {
        /**
         * ��Ƶ�ͼ���������� *
         */
        if (maxRow == 0) {
            maxRow = 20;
        }

        if (maxCol == 0) {
            maxCol = 60;
        }

        life = new Life(maxRow, maxCol);

        backPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(maxRow, maxCol));
        bottomPanel = new JPanel();
        rowList = new JComboBox();
        for (int i = 3; i <= 20; i++) {
            rowList.addItem(String.valueOf(i));
        }
        colList = new JComboBox();
        for (int i = 3; i <= 60; i++) {
            colList.addItem(String.valueOf(i));
        }
        rowList.setSelectedIndex(maxRow - 3);
        colList.setSelectedIndex(maxCol - 3);
        btnOk = new JButton("ȷ��");
        btnNextGeneration = new JButton("��һ��");
        btnBlock = new JButton[maxRow][maxCol];
        btnStart = new JButton("��ʼ�ݻ�");
        btnStop = new JButton("ֹͣ�ݻ�");
        btnExit = new JButton("�˳�");
        isSelected = new boolean[maxRow][maxCol];
        lblRow = new JLabel("����������");
        lblCol = new JLabel("����������");
        this.setContentPane(backPanel);

        backPanel.add(centerPanel, "Center");
        backPanel.add(bottomPanel, "South");

        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                btnBlock[i][j] = new JButton("");
                btnBlock[i][j].setBackground(Color.WHITE);
                centerPanel.add(btnBlock[i][j]);
            }
        }

        bottomPanel.add(lblRow);
        bottomPanel.add(rowList);
        bottomPanel.add(lblCol);
        bottomPanel.add(colList);
        bottomPanel.add(btnOk);
        bottomPanel.add(btnNextGeneration);
        bottomPanel.add(btnStart);
        bottomPanel.add(btnStop);
        bottomPanel.add(btnExit);

        // ���ô���
        this.setSize(900, 620);
        this.setResizable(false);
        this.setLocationRelativeTo(null); // �ô�������Ļ����

        // ����������Ϊ�ɼ���
        this.setVisible(true);

        // ע�������
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        btnOk.addActionListener(this);
        btnNextGeneration.addActionListener(this);
        btnStart.addActionListener(this);
        btnStop.addActionListener(this);
        btnExit.addActionListener(this);
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                btnBlock[i][j].addActionListener(this);
            }
        }
    }

    public GameFrame(String name) {
        super(name);
        initGui();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnOk) {
            frame.setMaxRow(rowList.getSelectedIndex() + 3);
            frame.setMaxCol(colList.getSelectedIndex() + 3);
            initGui();
            life = new Life(getMaxRow(), getMaxCol());
        } else if (e.getSource() == btnNextGeneration) {
            makeNextGeneration();
        } else if (e.getSource() == btnStart) {
            isRunning = true;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        makeNextGeneration();
                        boolean isSame = true;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        isDead = true;
                        for (int row = 1; row <= maxRow; row++) {
                            for (int col = 1; col <= maxCol; col++) {
                                if (life.getGrid()[row][col] != 0) {
                                    isDead = false;
                                    break;
                                }
                            }
                            if (!isDead) {
                                break;
                            }
                        }
                        if (isDead) {
                            JOptionPane.showMessageDialog(null, "������ʧ��~");
                            isRunning = false;
                            thread = null;
                        }
                    }
                }
            });
            thread.start();
        } else if (e.getSource() == btnStop) {
            isRunning = false;
            thread = null;
        } else if (e.getSource() == btnExit) {
            System.exit(0);
        } else {
            int[][] grid = life.getGrid();
            for (int i = 0; i < maxRow; i++) {
                for (int j = 0; j < maxCol; j++) {
                    if (e.getSource() == btnBlock[i][j]) {
                        isSelected[i][j] = !isSelected[i][j];
                        if (isSelected[i][j]) {
                            btnBlock[i][j].setBackground(Color.BLACK);
                            grid[i + 1][j + 1] = 1;
                        } else {
                            btnBlock[i][j].setBackground(Color.WHITE);
                            grid[i + 1][j + 1] = 0;
                        }
                        break;
                    }
                }
            }
            life.setGrid(grid);
        }
    }

    private void makeNextGeneration() {
        life.update();
        int[][] grid = life.getGrid();
        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                if (grid[i + 1][j + 1] == 1) {
                    btnBlock[i][j].setBackground(Color.BLACK);
                } else {
                    btnBlock[i][j].setBackground(Color.WHITE);
                }
            }
        }
    }
}