package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MainFrame extends JFrame {
    // 内容窗格
    private Container mContainer;


    // constructor
    public MainFrame() {
        mContainer = getContentPane();
        // 设置JFrame窗口标题
        setTitle("静态分层的RBAC演示");
        // 设置JFrame窗口尺寸
        setSize(1300, 700);
        // 往内容窗格中添加选项卡面板
        mContainer.add(getTabbedPane());
    }


    /**
     * 得到一个选项卡面板
     * @return 一个选项卡面板
     */
    private JTabbedPane getTabbedPane() {

        JTabbedPane tabbedPane = new JTabbedPane();

        JComponent p1 = getFilePanel();
        tabbedPane.addTab("文件面板", p1);
        tabbedPane.setMnemonicAt(0,KeyEvent.VK_1);

        JComponent p2 = getFilePanel();
        tabbedPane.addTab("文件面板", p2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        JComponent p3 = getUserPanel();
        tabbedPane.addTab("演示面板", p3);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        return tabbedPane;
    }


    private JPanel getFileSelectPanel(){
        JPanel panel = new JPanel(new FlowLayout());
        // TODO
        return panel;
    }

    /**
     * 得到一个文件选择组件
     * @return  一个自定义文件选择组件
     */
    private JPanel getMyFileChooser() {
        JPanel myFileChooser = new JPanel();

        JLabel label = new JLabel("所选目录路径：");
        JTextField jText = new JTextField(25);
        JButton button = new JButton("浏览");

        myFileChooser.add(label);
        myFileChooser.add(jText);
        myFileChooser.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int val = fc.showOpenDialog(null);    //文件打开对话框
                if (val == fc.APPROVE_OPTION) {
                    //正常选择文件
                    jText.setText(fc.getSelectedFile().toString());


                } else {
                    //未正常选择文件，如选择取消按钮
                    jText.setText("未选择文件");
                }
            }
        });
        return myFileChooser;
    }

    /**
     * 配置并返回文件面板
     * @return
     */
    private JPanel getFilePanel() {
        JPanel filePanel = new JPanel(new GridLayout(2, 1));
        filePanel.add(getMyFileChooser());
        return filePanel;
    }

    /**
     * 配置并返回用户面板
     * @return
     */
    private JPanel getUserPanel(){
        JPanel userPanel = new JPanel(new GridLayout(2, 1));
        userPanel.add(new Label("用户面板"));
        return userPanel;
    }


    /**
     * 配置并返回演示面板
     * @return
     */
    private JPanel getShowPanel(){
        JPanel showPanel = new JPanel(new GridLayout(2, 1));
        showPanel.add(new Label("演示面板"));
        return showPanel;
    }


    // main method
    public static void main(String[] agrs) {
        new MainFrame().setVisible(true);
    }
}
