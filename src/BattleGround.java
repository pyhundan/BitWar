import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;

/**
 * 主界面
 */
public class BattleGround extends JFrame{

    private JPanel showpanel;
    private JPanel controlpanel;
    private JButton start;
    private JButton setting;
    private JButton quit;
    private JButton history;

    /*
    系统自带策略：
    0，随机
    1，永远合作
    2，老实人探测器
    3，针锋相对
    4，永不原谅
    */
    LinkedList<Strategy> strategies = new LinkedList<>();
    LinkedList<Strategy> battler = new LinkedList<>();
    LinkedList<BattleHistory> histories = new LinkedList<>();
    int rounds = 100;


    public BattleGround(){
        strategies.addLast(new Strategy("随机.txt"));
        strategies.addLast(new Strategy("永远合作.txt"));
        strategies.addLast(new Strategy("老实人探测器.txt"));
        strategies.addLast(new Strategy("针锋相对.txt"));
        strategies.addLast(new Strategy("永不原谅.txt"));

        start = new JButton("Start");
        start.setPreferredSize(new Dimension(75,25));
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                beginbattle();
                System.out.println(histories.size());
            }
        });
        setting = new JButton("Setting");
        setting.setPreferredSize(new Dimension(75,25));
        setting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dosetting();
            }
        });
        history = new JButton("details");
        history.setPreferredSize(new Dimension(75,25));
        history.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] snames = new String[battler.size()];
                for(int i=0;i<battler.size();i++){
                    snames[i] = battler.get(i).name;
                }
                JFrame seach = new JFrame("详细对战结果");
                JComboBox cb1 = new JComboBox(snames);
                JComboBox cb2 = new JComboBox(snames);
                JTextField vs = new JTextField("VS");
                JButton yes = new JButton("确定");

                yes.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String a1 = (String)cb1.getSelectedItem();
                        String a2 = (String)cb2.getSelectedItem();
                        if(a1 == null || a2 == null){
                            JOptionPane.showMessageDialog(null,"请选择！");

                        }
                        else if(a1.equals(a2)){
                            JOptionPane.showMessageDialog(null,"一样的策略！");
                        }
                        else{
                            detailhistory(a1,a2);
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
                seach.setSize(300,100);
                seach.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            }
        });
        quit = new JButton("Quit");
        quit.setPreferredSize(new Dimension(75,25));
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        controlpanel = new JPanel();
        controlpanel.setPreferredSize(new Dimension(100,400));
        controlpanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,40));
        controlpanel.add(start);
        controlpanel.add(setting);
        controlpanel.add(history);
        controlpanel.add(quit);

        showpanel = new JPanel();
        showpanel.setPreferredSize(new Dimension(600,400));
        showpanel.setLayout(new FlowLayout());
        JTextArea jTextArea = new JTextArea();
        jTextArea.setPreferredSize(new Dimension(560,280));
        jTextArea.setLineWrap(true);
        jTextArea.setFont(new Font("黑体",Font.PLAIN,15));
        jTextArea.append("规则：每个策略两两对战100次（可在设置中更改）。");
        jTextArea.append("参战策略如下：\n");
        for(int i=0;i<strategies.size();i++){
            jTextArea.append((i+1) +","+ strategies.get(i).name + '\n');
        }
        showpanel.add(jTextArea);

        Container c =getContentPane();
        setLayout(new FlowLayout());
        add(showpanel);
        add(controlpanel);
        setVisible(true);
        setSize(800,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 启动策略对决
     */
    private void beginbattle(){
        histories.clear();
        int number = battler.size();
        for(int i=0;i<number;i++){
            Strategy row = battler.get(i);
            for (int j = i+1;j<number;j++){
                Strategy col = battler.get(j);
                BattleHistory temphisory= new BattleHistory(row,col);
                row.statementAnalyze.setBattleHistory(temphisory,1);
                col.statementAnalyze.setBattleHistory(temphisory,0);
                int rowdecide,coldecide;
                for(int k = 0;k<rounds;k++){
                    row.statementAnalyze.analysis();
                    rowdecide = row.statementAnalyze.feedback;
                    col.statementAnalyze.analysis();
                    coldecide = col.statementAnalyze.feedback;
                    Integer [] result = new Integer[4];
                    result[0] = rowdecide;
                    result[1] = coldecide;
                    result[2] = BattleHistory.ss1[rowdecide][coldecide];
                    result[3] = BattleHistory.ss2[rowdecide][coldecide];
                    temphisory.s1_cop_time += result[0];
                    temphisory.s2_cop_time += result[1];
                    temphisory.s1sum += result[2];
                    temphisory.s2sum += result[3];
                    temphisory.result.addLast(result);
                }
                histories.addLast(temphisory);
            }
        }
        dohistory();
    }

    /**
     * 点击设置后
     */
    private void dosetting(){
        JFrame set = new JFrame("Setting");

        JPanel roundset = new JPanel();
        JTextField round = new JTextField("回合数",5);
        JTextField roundinput = new JTextField(String.valueOf(rounds),8);
        round.setEditable(false);
        roundset.setPreferredSize(new Dimension(250,100));
        roundset.add(round);
        roundset.add(roundinput);

        LinkedList<JCheckBox> batters = new LinkedList<>();
        JCheckBox j;
        JPanel Strategyset = new JPanel();
        Strategyset.setLayout(new BoxLayout(Strategyset,BoxLayout.Y_AXIS));
        Strategyset.setPreferredSize(new Dimension(250,200));

        for (Strategy s:strategies) {
            j = new JCheckBox(s.name,true);
            Strategyset.add(j);
            batters.addLast(j);
        }
        JButton adding = new JButton("添加");
        adding.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                JFrame addstrategy = new JFrame("添加策略");
                JTextField name = new JTextField("名字",5);
                JTextField nameinput = new JTextField(10);
                name.setEditable(false);
                JTextArea strategyinput = new JTextArea();
                strategyinput.setEditable(true);
                JButton save = new JButton("保存");
                save.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String s = strategyinput.getText();
                        Strategy cur = new Strategy();
                        cur.name = nameinput.getText();
                        cur.wordAnalysis = new WordAnalysis(s);
                        cur.statementAnalyze = new StatementAnalyze((LinkedList<Token>)cur.wordAnalysis.analysis());
                        cur.statementAnalyze.analysis();
                        if(cur.statementAnalyze.fail == null){
                            strategies.addLast(cur);
                            JOptionPane.showMessageDialog(null,"保存成功");
                            addstrategy.dispose();
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"代码有误！\n"+cur.statementAnalyze.fail);
                        }
                        /*Strategyset.removeAll();
                        for (Strategy ss:strategies) {
                            JCheckBox jj = new JCheckBox(ss.name,true);
                            Strategyset.add(jj);
                            batters.addLast(jj);
                        }
                        Strategyset.validate();
                        addstrategy.dispose();*/
                    }
                });
                JButton savetolocal = new JButton("保存到本地");
                savetolocal.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String s = strategyinput.getText();
                        Strategy cur = new Strategy();
                        cur.name = nameinput.getText();
                        cur.wordAnalysis = new WordAnalysis(s);
                        cur.statementAnalyze = new StatementAnalyze((LinkedList<Token>)cur.wordAnalysis.analysis());
                        if(cur.statementAnalyze.fail == null){
                            strategies.addLast(cur);
                            try{
                                File f = new File(cur.name+".txt");
                                f.createNewFile();
                                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                                bw.write(s);
                                bw.close();
                                JOptionPane.showMessageDialog(null,"保存成功");
                                // set.validate();
                                addstrategy.dispose();
                            }
                            catch (Exception exc){
                                exc.printStackTrace();
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"代码有误！\n"+cur.statementAnalyze.fail);
                        }

                    }
                });
                JButton cancel = new JButton("取消");
                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        addstrategy.dispose();
                    }
                });
                JPanel buttonpanel = new JPanel();
                buttonpanel.add(save);
                buttonpanel.add(savetolocal);
                buttonpanel.add(cancel);

                JPanel namepanel = new JPanel();
                namepanel.add(name);
                namepanel.add(nameinput);

                addstrategy.add(namepanel);
                addstrategy.add(strategyinput);
                addstrategy.add(buttonpanel);

                Container c = addstrategy.getContentPane();
                c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
                addstrategy.setSize(500,400);
                addstrategy.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                addstrategy.setVisible(true);
            }
        });
        Strategyset.add(adding);

        JButton ok = new JButton("确认");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String r = roundinput.getText();
                if(!r.equals("")){
                    rounds = Integer.parseInt(r);
                }
                battler.clear();
                for (int i =0;i<batters.size();i++) {
                    JCheckBox jc = batters.get(i);
                    if(jc.isSelected()){
                        battler.addLast(strategies.get(i));
                    }
                }
                set.dispose();
            }
        });


        Container c= set.getContentPane();
        //c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));
        c.setLayout(new BorderLayout());
        set.setSize(250,400);
        set.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        set.add(roundset,BorderLayout.NORTH);
        set.add(Strategyset,BorderLayout.CENTER);
        set.add(ok,BorderLayout.SOUTH);
        set.setVisible(true);
    }

    private void dohistory(){
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        JTable jTable = new JTable(defaultTableModel);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(600,300));
        int number = battler.size();
        BattleHistory cur;
        defaultTableModel.addColumn("");
        for(Strategy s : battler){
            defaultTableModel.addColumn(s.name);
        }
        class sc{
            String name;
            int score;
        }
        sc a[] = new sc[battler.size()];
        for(int j=0;j<battler.size();j++) {
            String onehis [] = new String[number+1];
            Strategy s = battler.get(j);
            a[j] = new sc();
            a[j].name = s.name;
            int stotal=0;
            int tempindex =0;
            int border = battler.size();
            onehis[tempindex++] = s.name;
            boolean r_or_c;
            for(BattleHistory b : histories){
                if(s == b.s1){
                    r_or_c = true;
                }
                else if(s == b.s2){
                    r_or_c = false;
                }
                else continue;
                if(tempindex == j+1){
                    onehis[tempindex++] = "";
                }
                if(tempindex > border) break;
                if(r_or_c) {
                    onehis[tempindex++] = String.valueOf(b.s1sum-b.s2sum);
                    stotal += b.s1sum;
                }
                else {
                    onehis[tempindex++] = String.valueOf(b.s2sum-b.s1sum);
                    stotal += b.s2sum;
                }

            }
            defaultTableModel.addRow(onehis);
            a[j].score = stotal;
        }

        for(int i=0;i<a.length;i++){
            for(int j=0;j<a.length-i-1;j++){
                if(a[j].score < a[j+1].score){
                    sc temp = a[j+1];
                    a[j+1]=a[j];
                    a[j] = temp;
                }
            }
        }
        defaultTableModel.addRow(new String[]{"总计："});
        for(int i=0;i<a.length;i++){
            defaultTableModel.addRow(new String[]{"",a[i].name,a[i].score+""});
        }

        showpanel.removeAll();
        showpanel.add(jScrollPane);
        showpanel.validate();
    }

    /**
     * 详细对战结果
     * @param s1
     * @param s2
     */
    private void detailhistory(String s1,String s2){
        BattleHistory temp = null;
        for(BattleHistory b : histories){
            if(b.s1.name.equals(s1)||b.s1.name.equals(s2)){
                if(b.s2.name.equals(s1) || b.s2.name.equals(s2)){
                    temp = b;
                    break;
                }
            }
        }
        if(temp == null){
            return ;
        }
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        JTable jTable = new JTable(defaultTableModel);
        JScrollPane jScrollPane = new JScrollPane(jTable);
        jScrollPane.setPreferredSize(new Dimension(600,300));
        defaultTableModel.addColumn(s1+"的决定");
        defaultTableModel.addColumn(s2+"的决定");
        defaultTableModel.addColumn(s1+"的得分");
        defaultTableModel.addColumn(s2+"的得分");
        Integer re[];
        for(int i=0;i<rounds;i++){
            re = temp.result.get(i);
            defaultTableModel.addRow(re);
        }
        defaultTableModel.addRow(new String[]{temp.s1.name+"合作次数："+temp.s1_cop_time,temp.s2.name+"合作次数"+temp.s2_cop_time,
                                                temp.s1.name+"总得分"+temp.s1sum,temp.s2.name+"总得分"+temp.s2sum});

        showpanel.removeAll();
        showpanel.add(jScrollPane);
        showpanel.validate();
    }


    public static void main(String args[]){
        BattleGround  b = new BattleGround();

    }
}
