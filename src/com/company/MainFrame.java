package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

public class MainFrame extends JFrame {
    // 内容窗格
    private Container mContainer;

    // 需要进行访问控制的根目录
    private File mRootDir;
    // 根目录下的所有文件
    private String[] mFileList;

    // 可以进行“读取”和“写入”两种操作
    private String[] mOptList = new String[]{"读取", "写入"};

    // 选择目录文本标签
    private JTextField mTextField = new JTextField(25);

    // 文件列表组件
    private JList waitChooseFileList;
    // 操作列表组件
    private JList waitChooseOptList;


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
     *
     * @return 一个选项卡面板
     */
    private JTabbedPane getTabbedPane() {

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel p1 = getFilePanel();
        tabbedPane.addTab("文件面板", p1);
//        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JPanel p2 = getUserPanel();
        tabbedPane.addTab("用户面板", p2);
//        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        return tabbedPane;
    }


    /**
     * 配置并返回文件面板
     *
     * @return
     */
    private JPanel getFilePanel() {
        JPanel filePanel = new JPanel();

        JLabel label = new JLabel("所选目录路径：");
        JButton button = new JButton("浏览");

        filePanel.add(label);
        filePanel.add(mTextField);
        filePanel.add(button);


        waitChooseFileList = new JList(mFileList);
        waitChooseFileList.setBorder(BorderFactory.createTitledBorder("请选择要操作的文件："));
        waitChooseFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (mOptList != null) {
            mOptList = new String[]{"读取", "写入"};
        }
        waitChooseOptList = new JList(mOptList);
        waitChooseOptList.setBorder(BorderFactory.createTitledBorder("请选择要进行的操作："));
        waitChooseOptList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        filePanel.add(waitChooseFileList);
        filePanel.add(waitChooseOptList);


        // 如果有重新选择目录的需求的话，点击“浏览”按钮即可
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // 打开一个文件选择器
                JFileChooser fc = new JFileChooser();
                // 设置为只能选择目录
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                // 打开文件选择对话框并保存返回的状态
                int status = fc.showOpenDialog(null);

                if (status == fc.APPROVE_OPTION) {
                    // 如果选择成功，则修改本JFrame中的配置参数
                    mTextField.setText(fc.getSelectedFile().toString());
                    mRootDir = new File(fc.getSelectedFile().toString());
                    mFileList = fc.getSelectedFile().list();

                } else {
                    // 否则维持原样，什么也不干
                    System.out.println("cancel！");
                }
            }
        });

        return filePanel;
    }


    /**
     * 当调用此方法时，需要进行访问控制的根目录很可能已经发生了改变，
     * 相应地，成员变量mFileList中的内容很有可能也已经发生了改变
     * 因此需要调用此方法，来刷新UI界面中已经显示出的文件列表
     */
    private void invalidateUI() {
        // TODO
    }

    /**
     * 配置并返回用户面板
     *
     * @return
     */
    private JPanel getUserPanel() {
        JPanel userPanel = new JPanel(new GridLayout(2, 1));
        userPanel.add(new Label("用户面板"));
        return userPanel;
    }


    // main method
    public static void main(String[] agrs) {

        // 先创建好主界面，但不显示
        MainFrame mainFrame = new MainFrame();

        // 首先打开一个文件选择器
        JFileChooser fc = new JFileChooser();
        // 只能选择目录
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // 打开文件选择对话框并保存返回的状态
        int status = fc.showOpenDialog(null);


        if (status == fc.APPROVE_OPTION) {
            // 如果选择成功，配置mainFrame中的初始参数
            mainFrame.mTextField.setText(fc.getSelectedFile().toString());
            mainFrame.mRootDir = new File(fc.getSelectedFile().toString());
            // BUG
            mainFrame.mFileList = fc.getSelectedFile().list();
            mainFrame.setVisible(true);

        } else {
            // 异常退出(点击了取消按钮)，则直接退出
            System.out.println("quit！");
        }
    }
}