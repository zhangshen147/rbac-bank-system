package com.zhangshen147;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
    private JList mFileListOnUI;
    // 操作列表组件
    private JList mOptionListOnUI;


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
        tabbedPane.addTab("角色管理", p2);
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
        BoxLayout vertical = new BoxLayout(filePanel, BoxLayout.Y_AXIS);
        filePanel.setLayout(vertical);

        JLabel label = new JLabel("所选目录路径：");
        JButton button = new JButton("浏览");

        // 用来显示选择了什么文件夹的小面板（上面）
        JPanel chooser = new JPanel();
        chooser.add(label);
        chooser.add(mTextField);
        chooser.add(button);

        // 这里没必要为JList传入数据源参数，因为当这个组件被构造出来的时候，
        // 我们还无法定位到数据源究竟处于哪个目录
        mFileListOnUI = new JList();
        mFileListOnUI.setFixedCellWidth(150);
        mFileListOnUI.setBorder(BorderFactory.createTitledBorder("请选择文件："));
        mFileListOnUI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        if (mOptList != null) {
            mOptList = new String[]{"读取", "写入"};
        }
        mOptionListOnUI = new JList(mOptList);
        mOptionListOnUI.setFixedCellWidth(100);
        mOptionListOnUI.setBorder(BorderFactory.createTitledBorder("访问类型："));
        mOptionListOnUI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 用来选择文件和要进行的操作的面板（下面）
        JPanel selected = new JPanel();
        selected.add(mFileListOnUI);
        selected.add(mOptionListOnUI);

        // 提交按钮
        JButton bt_commit  = new JButton("提交");
        bt_commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = (String)mFileListOnUI.getSelectedValue();
                int selectedOption = getOptionCode((String)mOptionListOnUI.getSelectedValue());
                if (selectedOption == -1){
                    JOptionPane.showConfirmDialog(null, "操作成功");
                    return;
                }

                boolean res = RbacController.testPermission("zhangshen", mRootDir.getAbsolutePath() + selectedFile, selectedOption);
                if (res == true){
                    JOptionPane.showConfirmDialog(null, "操作成功");
                }else {
                    JOptionPane.showConfirmDialog(null, "操作失败");
                }

            }
        });

        // 添加上下两个面板，以及一个提交按钮
        filePanel.add(chooser);
        filePanel.add(selected);
        filePanel.add(bt_commit);


        // 如果有重新选择目录的需求的话，点击“浏览”按钮即可
        button.addActionListener(new ActionListener() {

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


    private int getOptionCode(String str) {

        if (str == null){
            return -1;
        }

        if (str.equals("读取")){
            return RbacController.PERMISSION_READ;
        }else if (str.equals("写入")){
            return RbacController.PERMISSION_WRITE;
        }

        // 此时说明发生了异常
        return -1;
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
            mainFrame.mFileList = fc.getSelectedFile().list();
            mainFrame.mFileListOnUI.setListData(mainFrame.mFileList);
            mainFrame.setVisible(true);

        } else {
            // 异常退出(点击了取消按钮)，则直接退出
            System.out.println("quit！");
        }
    }
}