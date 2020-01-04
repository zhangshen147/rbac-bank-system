package com.zhangshen147;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URL;

public class MainFrame extends JFrame {
    private static final Logger logger = Logger.getLogger(MainFrame.class);

    // 显示信息
    public static final String MESSAGE_WRITE_FAIL = "当前用户尚未拥有对这个文件的写入权限";
    public static final String MESSAGE_WRITE_SUCCESS = "请在点击确认按钮以后输入要写入的内容...";
    public static final String MESSAGE_READ_FAIL = "当前用户尚未拥有对这个文件的读取权限";
    public static final String MESSAGE_READ_SUCCESS = "点击确认按钮以后将为您展示文件内容...";
    public static final String MESSAGE_WARNING = "请确保两个选项框都已经被勾选了！";
    private static final String LABEL_YES = "√";
    private static final String LABEL_NO = " ";

    // 窗口尺寸
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 700;


    // 角色类型
    private static final String[] ROLE_TYPES = new String[]{"出纳员", "分行管理者", "顾客", "系统管理员", "审计员"};
    // 操作类型
    private static final String[] OPERATION_TYPES = new String[]{"读取", "写入"};

    // 用户列表，数据本应从数据库中读取
    private String[] mUserList = new String[]{"张三", "李四", "王五", "赵六"};
    // 文件列表，从当前目录下读取，但演示时为了保证简单性，应该只包含几个固定的文件，否则还得动态配置访问控制规则
    private String[] mFileList;

    // JTable中的填充内容
    private String[][] mRowData = new String[][]{
            {"张三", LABEL_YES, LABEL_NO, LABEL_NO, LABEL_NO, LABEL_NO},
            {"李四", LABEL_NO, LABEL_YES, LABEL_NO, LABEL_NO, LABEL_NO},
            {"王五", LABEL_NO, LABEL_NO, LABEL_YES, LABEL_NO, LABEL_NO},
            {"赵六", LABEL_NO, LABEL_NO, LABEL_NO, LABEL_YES, LABEL_NO}
    };

    // 当前用户
    String mCurrentUser = "张三";


    // 需要进行访问控制的根目录
    private File mRootDir;

    /**
     * 以下是几个特殊UI组件，它们由于需要被其它方法或匿名内部类访问而必须
     * 声明为成员变量以保存相关信息，否则引用传递将会变得很麻烦
     */
    private JTextField mTextField = new JTextField(25);
    // 文件列表组件，由于在回调中被引用，因此声明为成员变量
    private JList mFileListOnUI;
    // 操作列表组件，由于需要动态更新数据，因此声明为成员变量
    private JList mOptionListOnUI;
    // 用于展示用户到角色映射关系的表格
    private JTable mMapTable;


    // Constructor
    public MainFrame() {
        // 设置整个应用窗口的标题
        setTitle("RBAC2演示应用");
        // 为应用窗口设置大小尺寸
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        // 应用窗口居中显示
        setLocationRelativeTo(null);
        // 向内容窗格中添加真正可以显示出来的选项卡面板组件，其中又内嵌了许多其它组件
        getContentPane().add(getTabbedPane());
    }


    /**
     * 构成该Swing应用的主要组件
     *
     * @return 一个选项卡面板
     */
    private JTabbedPane getTabbedPane() {

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel p1 = getFileManagerPanel();
        tabbedPane.addTab("文件面板", p1);
//        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JPanel p2 = getRoleManagerPanel();
        tabbedPane.addTab("角色管理", p2);
//        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        return tabbedPane;
    }


    /**
     * 该Swing界面主要由两个选项卡面板组成，
     * 这是其中之一的用作文件管理的选项卡
     *
     * @return 一个JPanel类型的文件管理选项卡
     */
    private JPanel getFileManagerPanel() {
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

        mOptionListOnUI = new JList(OPERATION_TYPES);
        mOptionListOnUI.setFixedCellWidth(100);
        mOptionListOnUI.setBorder(BorderFactory.createTitledBorder("访问类型："));
        mOptionListOnUI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 用来选择文件和要进行的操作的面板（下面）
        JPanel selected = new JPanel();
        selected.add(mFileListOnUI);
        selected.add(mOptionListOnUI);

        // 提交按钮
        JButton bt_commit = new JButton("提交");
        bt_commit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String selectedFile = (String) mFileListOnUI.getSelectedValue();
                int selectedOption = getOptionCode((String) mOptionListOnUI.getSelectedValue());

                if (selectedFile == null || selectedOption == -1) {
                    JOptionPane.showMessageDialog(null, MESSAGE_WARNING);
                    return;
                }

                /**
                 * 读取操作相关权限
                 */
                if (selectedOption == RbacController.PERMISSION_READ) {
                    boolean res = RbacController.testPermission("zhangshen", mRootDir.getAbsolutePath() + selectedFile, RbacController.PERMISSION_READ);
                    if (res == true) {
                        // 如果可以读
                        JOptionPane.showMessageDialog(MainFrame.this, MESSAGE_READ_SUCCESS);
                        File fileWaitToRead = new File(mRootDir, (String) mFileListOnUI.getSelectedValue());
                        showFileContent(fileWaitToRead);
                    } else {
                        // 如果不可读
                        JOptionPane.showMessageDialog(MainFrame.this, MESSAGE_READ_FAIL);
                    }
                }

                /**
                 * 写入权限相关操作
                 */
                if (selectedOption == RbacController.PERMISSION_WRITE) {
                    boolean res = RbacController.testPermission("zhangshen", mRootDir.getAbsolutePath() + selectedFile, RbacController.PERMISSION_READ);
                    if (res == true) {
                        // 如果可以写
                        JOptionPane.showMessageDialog(MainFrame.this, MESSAGE_WRITE_SUCCESS);
                        File fileWaitToWrite = new File(mRootDir, (String) mFileListOnUI.getSelectedValue());
                        logger.debug("要读取的文件名：" + fileWaitToWrite);
                        String text = JOptionPane.showInputDialog(MainFrame.this, "要写入文件的内容：");
                        logger.debug("要写入文件的内容：" + text);
                        try {
                            FileWriter writer = new FileWriter(fileWaitToWrite, true);
                            writer.write(text);
                            writer.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // 如果不可写
                        JOptionPane.showMessageDialog(MainFrame.this, MESSAGE_WRITE_FAIL);
                    }

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
                    logger.debug("选择目录不成功");
                }
            }
        });

        return filePanel;
    }

    /**
     * 弹出一个窗口来显示文件中的内容
     *
     * @param file 需要被显示的文件
     */
    private void showFileContent(File file) {
        String str = null;
        try {
            FileReader reader = new FileReader(file);
            char[] charText = new char[1024 * 1024];
            reader.read(charText);
            str = new String(charText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame showTextJFrame = new JFrame();
        showTextJFrame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        showTextJFrame.add(new TextArea(str));
        showTextJFrame.setVisible(true);
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
     * 该Swing界面主要由两个选项卡面板组成，
     * 这是其中之一的用作角色管理的选项卡
     *
     * @return 一个JPanel类型的角色管理选项卡
     */
    private JPanel getRoleManagerPanel() {

        // 总体布局是垂直的
        JPanel rolePanel = new JPanel();
        BoxLayout vertical = new BoxLayout(rolePanel, BoxLayout.Y_AXIS);
        rolePanel.setLayout(vertical);

        // 当前用户面板
        JPanel chooseUserPanel = getCurrentUserPanel();

        // 得到添加角色面板，需要传入自行实现的回调接口
        JPanel addRolePanel = getChildPanel(true);

        // 得到删除角色面板
        JPanel delRolePanel = getChildPanel(false);

        // 得到展示列表
        mMapTable = getMapTable();
        JScrollPane scrollPane = new JScrollPane(mMapTable);
        scrollPane.setSize(200, 50);
        mMapTable.setFillsViewportHeight(true);

        // 利用网格布局不优雅地调整scrollPane的大小
        GridLayout layout = new GridLayout(4, 3);
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        // 将其余组件设置为不可见
        for (int i = 0; i < 12; i++) {
            if (i == 4) {
                panel.add(scrollPane);
                continue;
            }
            JButton blank = new JButton();
            blank.setVisible(false);
            panel.add(blank);
        }

        // 添加到主面板上
        rolePanel.add(chooseUserPanel);
        rolePanel.add(addRolePanel);
        rolePanel.add(delRolePanel);
        rolePanel.add(panel);

        return rolePanel;
    }

    // 尝试修改表格中的内容，并写入到数据库
    private boolean modifyTable(String user, String role, boolean isAdd) {
        int rows = mMapTable.getRowCount();
        int cols = mMapTable.getColumnCount();

        int rowAt = 0, colAt = 0;

        // 获取行号
        for (int i = 0; i < rows; i++) {
            if (mRowData[i][0].equals(user)) {
                rowAt = i;
            }
        }

        // 获取列号
        for (int j = 1; j < cols; j++) {
            if (ROLE_TYPES[j - 1].equals(role)) {
                colAt = j;
            }
        }

        logger.debug("行号：" + rowAt + ", 列号" + colAt);
        if (isAdd) {
            // TODO 检查是否可以添加权限
            mRowData[rowAt][colAt] = LABEL_YES;
            mMapTable.setValueAt(LABEL_YES, rowAt, colAt);
            // TODO 写入数据库
        } else {
            // TODO 检查是否还能再删除权限
            mRowData[rowAt][colAt] = LABEL_NO;
            mMapTable.setValueAt(LABEL_NO, rowAt, colAt);
            // TODO 写入数据库
        }


//        if (isAdd) {
//            mMapTable.setValueAt(LABEL_YES, rowAt, colAt);
//        }

        return true;
    }

    /**
     * 用于显示所有用户与角色之间的映射关系的表格，
     * 用户和角色之间的关系不一定是一对一的，也可能是多对一的
     *
     * @return 一个JTable类型的表格组件
     */
    private JTable getMapTable() {

        String[] columnName = new String[]{"员工", "出纳员", "分行管理者", "顾客", "系统管理员", "审计员"};
        JTable table = new JTable(mRowData, columnName);
        table.setSize(200, 50);
        setTableColumnCenter(table);
        setTableColumnWidth(table);

        // 输出坐标位置
        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent event) {
                int row = table.rowAtPoint(event.getPoint());
                int col = table.columnAtPoint(event.getPoint());
                logger.debug("当前点击的行："+row);
                logger.debug("当前点击的行："+col);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });
        return table;
    }

    /**
     * 为一个表格组件设置列宽
     * 没有生效，不过暂时保留
     *
     * @param table
     */
    private void setTableColumnWidth(JTable table) {
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(80);
        }
    }

    /**
     * 得到一个添加/删除角色面板
     *
     * @param isAddRole true表示这是一个添加角色面板，false表示这是一个删除角色面板
     * @return 一个横向的面板，包括两个下拉菜单和一个确认按钮
     */
    private JPanel getChildPanel(boolean isAddRole) {
        JPanel addOrDelRolePanel = new JPanel();
        addOrDelRolePanel.setSize(100, 100);

        JComboBox menu1 = new JComboBox(mUserList);
        JComboBox menu2 = new JComboBox(ROLE_TYPES);
        JButton button = new JButton();

        // 添加不同的监听器
        if (isAddRole == true) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    modifyTable((String) menu1.getSelectedItem(), (String) menu2.getSelectedItem(), true);
                    JOptionPane.showMessageDialog(MainFrame.this, "角色添加成功！");
                }
            });
        } else {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    modifyTable((String) menu1.getSelectedItem(), (String) menu2.getSelectedItem(), false);
                    JOptionPane.showMessageDialog(MainFrame.this, "角色删除成功！");
                }
            });
        }

        // 设置不同的标签内容
        if (isAddRole == true) {
            button.setLabel("添加");
            addOrDelRolePanel.add(new JLabel("添加角色："));
        } else {
            button.setLabel("删除");
            addOrDelRolePanel.add(new JLabel("删除角色："));
        }

        // 将以上组件依次添加到面板上
        addOrDelRolePanel.add(menu1);
        addOrDelRolePanel.add(menu2);
        addOrDelRolePanel.add(button);

        return addOrDelRolePanel;
    }

    /**
     * 获取一个当前用户组件
     */
    private JPanel getCurrentUserPanel() {
        JPanel panel = new JPanel();
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGHT / 4);
//        panel.setBackground(Color.green);

        // 构造带图标的用户标签
        URL avatarPath = getClass().getResource("/avatar.jpg");
        ImageIcon icon = new ImageIcon(avatarPath);
        JLabel userLabel = new JLabel(mCurrentUser, JLabel.CENTER);
        userLabel.setIcon(icon);
        userLabel.setBackground(Color.yellow);

        // 为用户标签设置监听
        userLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                Object userName = JOptionPane.showInputDialog(MainFrame.this, "请选择用户名", "切换用户", 1, null, mUserList, mCurrentUser);
                if (userName != null) {
                    // 这里判空是因为存在打开了下拉菜单但没有进行任何操作的可能性
                    mCurrentUser = (String) userName;
                    userLabel.setText(mCurrentUser);
                    JOptionPane.showMessageDialog(MainFrame.this, "当前用户已切换为:" + mCurrentUser);
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        // 网格布局
        GridLayout layout = new GridLayout(2, 8);
        panel.setLayout(layout);
        // 将其余组件设置为不可见
        for (int i = 0; i < 16; i++) {
            if (i == 7) {
                panel.add(userLabel);
                continue;
            }

            JButton blank = new JButton();
            blank.setVisible(false);
            panel.add(blank);
        }
        return panel;
    }


    /**
     * 得到需要对文件进行的操作类型
     *
     * @param str 操作的字符类型
     * @return 操作的数值表示类型
     */
    private int getOptionCode(String str) {

        if (str == null) {
            return -1;
        }

        if (str.equals("读取")) {
            return RbacController.PERMISSION_READ;
        } else if (str.equals("写入")) {
            return RbacController.PERMISSION_WRITE;
        }

        // 此时说明发生了异常
        return -1;
    }

    /**
     * 设置一个表格组件的数据显示位置为居中
     *
     * @param table 任意J一个Table类型的表格
     */
    public void setTableColumnCenter(JTable table) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, r);
    }

    // main method
    public static void main(String[] agrs) {

        // 先把主界面创建出来，但不显示
        MainFrame mainFrame = new MainFrame();

        // 首先打开一个文件选择器
        JFileChooser fc = new JFileChooser();
        // 设置为只能选择目录
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
            logger.debug("取消选择文件夹");
        }
    }
}