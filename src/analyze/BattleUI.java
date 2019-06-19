package analyze;

import modle.Users;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.LinkedList;

public class BattleUI extends JFrame{

    private JMenuBar menuBar;
    private JMenu jMenu_op;
    private JMenu jMenu_about;
    private JPanel showpanel;
    private JPanel showdetial;

    private JMenuItem start_item;
    private JMenuItem setting_item;
    private JMenuItem add_item;
    private JMenuItem detial_item;
    private JMenuItem quit_item;
    private JMenuItem about_item;
    LinkedList<Users> users=new LinkedList<>();//存储存在策略
    LinkedList<Users> battler=new LinkedList<>();//存储选择的策略
    LinkedList<Array> histories=new LinkedList<>();//历史记录
    String rule="";
    int rounds=5;
    boolean flag=false;

    public BattleUI(){
        users.addLast(new Users("随机1.txt"));
        users.addLast(new Users("永远合作1.txt"));
        users.addLast(new Users("老实人探测器1.txt"));
        users.addLast(new Users("针锋相对1.txt"));
        users.addLast(new Users("永不原谅1.txt"));

        menuBar=new JMenuBar();
        jMenu_op=new JMenu("操作");
        jMenu_about=new JMenu("关于");
        start_item=new JMenuItem("开始");
        start_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flag==false)
                {
                    JOptionPane.showMessageDialog(null,"请点击设置选择策略");
                }
                else beginwar();
            }
        });


        setting_item=new JMenuItem("设置");
        setting_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag=true;
                setSetting();
            }
        });
        add_item=new JMenuItem("添加策略");
        add_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUsers();
            }
        });
        detial_item=new JMenuItem("对战细节");
        detial_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flag==false)
                {
                    JOptionPane.showMessageDialog(null,"请点击设置选择策略");
                }
                else {
                   showdetial();
                }
            }
        });
        quit_item=new JMenuItem("退出");
        quit_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        about_item=new JMenuItem("关于");
        about_item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"编译原理实验二");
            }
        });

        jMenu_op.add(start_item);
        jMenu_op.add(setting_item);
        jMenu_op.add(add_item);
        jMenu_op.add(detial_item);
        jMenu_op.add(quit_item);
        jMenu_about.add(about_item);
        menuBar.add(jMenu_op);
        menuBar.add(jMenu_about);
        setJMenuBar(menuBar);

        showdetial=new JPanel();
        showdetial.setLayout(new BoxLayout(showdetial,BoxLayout.Y_AXIS));

        showdetial.setPreferredSize(new Dimension(600,560));
        JTextArea ditialArea=new JTextArea();
        ditialArea.setPreferredSize(new Dimension(560,560));
        ditialArea.setFont(new Font("黑体",Font.PLAIN,15));
        ditialArea.append("显示对战细节");
        showdetial.add(ditialArea);

        showpanel=new JPanel();
        showpanel.setPreferredSize(new Dimension(560,560));
        showpanel.setLayout(new FlowLayout());
        JTextArea jTextArea=new JTextArea();
        jTextArea.setPreferredSize(new Dimension(560,560));
        jTextArea.setFont(new Font("黑体",Font.PLAIN,15));
        jTextArea.append("规则：每个策略两两对战5次");
        jTextArea.append("参考策略如下:\n");
        for(int i=0;i<users.size();i++)
        {
            jTextArea.append((i+1)+","+users.get(i).name+'\n');
        }
        rule=getFile("文法规则.txt");
        jTextArea.append(rule);
        jTextArea.setLineWrap(true);
        showpanel.add(jTextArea);

        Container c=getContentPane();
        setLayout(new FlowLayout());
        add(showpanel);
        add(showdetial);
        setVisible(true);
        setSize(1200,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void beginwar()
    {
        histories.clear();
        int number=battler.size();
        for(int i=0;i<number;i++)
        {
            Users row =battler.get(i);
            for(int j=i+1;j<number;j++)
            {
                Users col=battler.get(j);
                Array tempArray=new Array(row,col);
                row.stateAnalyze.setArray(tempArray,1);
                col.stateAnalyze.setArray(tempArray,0);
                int rowdecide,coldecide;
                for(int k=0;k<rounds;k++)
                {
                    row.stateAnalyze.start_analyse();//进行语法分析
                    rowdecide=row.stateAnalyze.back;//取出语法分析出得结果
                    col.stateAnalyze.start_analyse();
                    coldecide=col.stateAnalyze.back;
                    Integer [] result=new Integer[4];
                    result[0]=rowdecide;//判断是否合作
                    result[1]=coldecide;
                    result[2]= Array.s1[rowdecide][coldecide];//本轮获得的分数
                    result[3]= Array.s2[rowdecide][coldecide];
                    tempArray.s1_time=tempArray.s1_time+result[0];
                    tempArray.s2_time=tempArray.s2_time+result[1];
                    tempArray.s1_sum=tempArray.s1_sum+result[2];
                    tempArray.s2_sum=tempArray.s2_sum+result[3];
                    tempArray.result.addLast(result);
                }
                histories.addLast(tempArray);
            }
        }
        dohistory();
    }
    public void setSetting() {
        JFrame set=new JFrame("设置");
        JPanel setPanel=new JPanel();
        JTextField round=new JTextField("回合数",5);
        JTextField input_round=new JTextField(String.valueOf(rounds),8);
        round.setEditable(false);
        setPanel.setPreferredSize(new Dimension(250,100));
        setPanel.add(round);
        setPanel.add(input_round);

        LinkedList<JCheckBox> base=new LinkedList<>();
        JCheckBox jCheckBox;
        JPanel setUsers=new JPanel();
        setUsers.setLayout(new BoxLayout(setUsers,BoxLayout.Y_AXIS));
        setUsers.setPreferredSize(new Dimension(250,200));

        for (Users s:users)
        {
            jCheckBox=new JCheckBox(s.name,true);
            setUsers.add(jCheckBox);
            base.addLast(jCheckBox);
        }

        JButton enter=new JButton("确认");
        enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextArea show=new JTextArea();
                JScrollPane jScrollPane = new JScrollPane(show);
                jScrollPane.setPreferredSize(new Dimension(600,560));
                String r = input_round.getText();
                if (!r.equals("")) {
                    rounds = Integer.parseInt(r);
                }
                battler.clear();
                for (int i = 0; i < base.size(); i++) {
                    JCheckBox jc = base.get(i);
                    if (jc.isSelected()) {
                        battler.addLast(users.get(i));
                        String temp=users.get(i).name;
                        String k=users.get(i).name+"\n"+getFile(temp);
                        show.append(k);
                    }
                }
                showdetial.removeAll();
                showdetial.add(jScrollPane);
                showdetial.validate();
                set.dispose();
            }
        });

        Container c= set.getContentPane();
        c.setLayout(new BorderLayout());
        set.setSize(250,400);
        set.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        set.add(setPanel,BorderLayout.NORTH);
        set.add(setUsers,BorderLayout.CENTER);
        set.add(enter,BorderLayout.SOUTH);
        set.setVisible(true);

    }

    //显示历史对战结果
    public void detailhistory(String s1,String s2)
    {
        Array temp=null;
        for (Array s:histories)
        {
            if (s.ss1.name.equals(s1)||s.ss1.name.equals(s2))
            {
                if (s.ss2.name.equals(s1)||s.ss2.name.equals(s2))
                {
                    temp=s;
                    break;
                }
            }
        }
        if (temp==null)
        {
            return;
        }
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        JTable jTable = new JTable(defaultTableModel);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(600,560));
        defaultTableModel.addColumn(s1+"的决定");
        defaultTableModel.addColumn(s2+"的决定");
        defaultTableModel.addColumn(s1+"的得分");
        defaultTableModel.addColumn(s2+"的得分");
        Integer re[];
        for(int i=0;i<rounds;i++){
            re = temp.result.get(i);
            defaultTableModel.addRow(re);
        }
        defaultTableModel.addRow(new String[]{temp.ss1.name+"合作次数："+temp.s1_time,temp.ss2.name+"合作次数"+temp.s2_time,
                temp.ss1.name+"总得分"+temp.s1_sum,temp.ss2.name+"总得分"+temp.s2_sum});

        showdetial.removeAll();
        showdetial.add(jScrollPane);
        showdetial.validate();

    }
    //对战的历史记录
    public void dohistory(){
        DefaultTableModel defaultTableModel=new DefaultTableModel();
        JTable jTable=new JTable(defaultTableModel);
        JScrollPane jScrollPane=new JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(600,560));
        int number=battler.size();
        defaultTableModel.addColumn("");
        for (Users s:battler)
        {
            defaultTableModel.addColumn(s.name);
        }
        class sc{
            String name;int score;
        }
        sc a[]=new sc[battler.size()];
        for (int j=0;j<battler.size();j++)
        {
            String one[]=new String[number+1];
            Users s=battler.get(j);
            a[j]=new sc();
            a[j].name=s.name;

            int stotal=0;
            int temp_index=0;
            int border=battler.size();
            one[temp_index++]=s.name;
            boolean r_or_c;
            for (Array b:histories)
            {
                if (s==b.ss1)
                {
                    r_or_c=true;
                }
                else if (s==b.ss2)
                {
                    r_or_c=false;
                }
                else continue;
                if (temp_index==j+1)
                {
                    one[temp_index++]="";
                }
                if (temp_index>border) break;
                if (r_or_c)
                {
                    one[temp_index++]=String.valueOf(b.s1_sum-b.s2_sum);
                    stotal+=b.s1_sum;
                }
                else{
                    one[temp_index++]=String.valueOf(b.s2_sum-b.s1_sum);
                    stotal+=b.s2_sum;
                }
            }
            defaultTableModel.addRow(one);
            a[j].score=stotal;
        }
        for (int i=0;i<a.length;i++)
        {
            for (int j=0;j<a.length-i-1;j++)
            {
                if (a[j].score<a[j+1].score)
                {
                    sc temp=a[j+1];
                    a[j+1]=a[j];
                    a[j]=temp;
                }
            }
        }
        defaultTableModel.addRow(new String []{"总计："});
        for (int i=0;i<a.length;i++)
        {
            defaultTableModel.addRow(new String[]{"",a[i].name,a[i].score+""});
        }

        showdetial.removeAll();
        showdetial.add(jScrollPane);
        showdetial.validate();
    }
    public void addUsers()
    {
        JFrame addbase=new JFrame("添加");
        JTextField name=new JTextField("名字",5);
        JTextField input_name=new JTextField(10);
        name.setEditable(false);
        JTextArea input_base=new JTextArea();
        input_base.setEditable(true);
        String s=input_base.getText();
        Users cur=new Users();
        //进行词法语法分析
        cur.name=input_name.getText();
        cur.wordAnalyze=new WordAnalyze(s);
        cur.stateAnalyze=new StateAnalyze((LinkedList<Toke>)cur.wordAnalyze.analyse());
        cur.stateAnalyze.start_analyse();

        JButton save=new JButton("保存");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (cur.stateAnalyze.fail_message ==null)
                {
                    users.addLast(cur);
                    JOptionPane.showMessageDialog(null,"保存成功");
                    addbase.dispose();
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"保存失败!\n"+"原因："+cur.stateAnalyze.fail_message);
                }
            }
        });
        JButton local_save=new JButton("保存到本地");
        local_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur.stateAnalyze.fail_message ==null)
                {
                    users.addLast(cur);
                    try {
                        File file=new File(cur.name+".txt");
                        file.createNewFile();
                        BufferedWriter bw=new BufferedWriter(new FileWriter(file));
                        bw.write(s);
                        bw.close();
                        JOptionPane.showMessageDialog(null,"成功");
                        addbase.dispose();

                    }
                    catch (Exception exc)
                    {
                        exc.printStackTrace();
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"保存失败\n"+"原因："+cur.stateAnalyze.fail_message);
                }
            }
        });
        JButton cancel=new JButton("取消");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addbase.dispose();
            }
        });
        JPanel buttonPanel=new JPanel();
        buttonPanel.add(save);
        buttonPanel.add(local_save);
        buttonPanel.add(cancel);

        JPanel namePanel=new JPanel();
        namePanel.add(name);
        namePanel.add(input_name);

        addbase.add(namePanel);
        addbase.add(input_base);
        addbase.add(buttonPanel);

        Container c = addbase.getContentPane();
        c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        addbase.setSize(500,400);
        addbase.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addbase.setVisible(true);

    }

    public void showdetial()
    {
        String[] snames = new String[battler.size()];
        for (int i = 0; i < battler.size(); i++) {
            snames[i] = battler.get(i).name;
        }
        JFrame seach = new JFrame("详细对战结果");
        JComboBox cb1 = new JComboBox(snames);
        JComboBox cb2 = new JComboBox(snames);
        JTextField vs = new JTextField("vs");
        JButton yes = new JButton("确定");

        yes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String a1 = (String) cb1.getSelectedItem();
                String a2 = (String) cb2.getSelectedItem();
                if (a1 == null || a2 == null) {
                    JOptionPane.showMessageDialog(null, "选择");
                } else if (a1.equals(a2)) {
                    JOptionPane.showMessageDialog(null, "不能选择一样的策略");
                } else {
                    detailhistory(a1, a2);
                    seach.dispose();
                }
            }
        });
        vs.setEditable(false);
        seach.setLayout(new FlowLayout());
        seach.add(cb1);
        seach.add(vs);
        seach.add(cb2);
        seach.add(yes);
        seach.setVisible(true);
        seach.setSize(400, 100);
        seach.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    public String getFile(String filepath)
    {
        String a="";
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(filepath));
            String temp=bufferedReader.readLine();
            while(temp!=null)
            {
                a=a+temp+"\n";
                temp=bufferedReader.readLine();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return a;
    }
}
