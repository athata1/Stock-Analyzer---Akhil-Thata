import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.font.*;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.swing.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StockAnalyzer extends JPanel {
    
    private String stockURL;
    private String ticker;
    private long time2;
    private long time1;
    private BufferedReader reader;
    private ArrayList<Data> data;
    private double ratio;
    private TreeMap<Integer,Color> yearMap;
    private String mode;
    String date;
    JFrame frame;
    public StockAnalyzer()
    {
        getValues();

        //Create command frame
        JFrame f=new JFrame("Commands");
        JButton b1=new JButton("Regular");
        JButton b2=new JButton("Log");
        JButton b3=new JButton("Gradiant");
        JTextField text = new JTextField("Enter Ticker Here");
        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ticker = text.getText();
                getData(date,ticker);
                repaint();
            }
        });
        JTextField dateText = new JTextField("Enter Start Date Here");
        dateText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pattern pattern = Pattern.compile("(0[0-9]||1[0-2])/([0-2][0-9]||3[0-1])/((19|20)\\d\\d)");
                Matcher matcher = pattern.matcher(dateText.getText());
                //Get date1
                if (matcher.matches())
                {
                    date = dateText.getText();
                    getData(date,ticker);
                    repaint();
                }
            }
        });
        text.setBounds(50,200, 95,30);
        b1.setBounds(50,50,95,30);
        b2.setBounds(50,100,95,30);
        b3.setBounds(50,150,95,30);
        dateText.setBounds(50,250,95,30);
        b1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mode = "regular";
                getData(date,ticker);
                repaint();
            }
        });
        b2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mode = "log";
                getData(date,ticker);
                repaint();
            }
        });
        b3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mode = "gradient";
                repaint();
            }
        });
        f.add(b1);
        f.add(b2);
        f.add(b3);
        f.add(text);
        f.add(dateText);
        f.setSize(200,400);
        f.setLayout(null);
        f.setVisible(true);
    }
    public void getData(String date, String ticker)
    {

        //Get time in Milliseconds
        if (date != null) {
            String[] dateData = date.split("/");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, Integer.parseInt(dateData[0])-1);
            c.set(Calendar.YEAR, Integer.parseInt(dateData[2]));
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateData[1]));
            time1 = c.getTimeInMillis() / 1000;
        }
        else
            time1 = System.currentTimeMillis() - 365*24*60*60*1000;
        time2 = System.currentTimeMillis();

        //Load Stock Data
        Data.maxCloseValue = 0;
        stockURL = "https://query1.finance.yahoo.com/v7/finance/download/" + ticker + "?period1=" + time1 + "&period2=" + time2 + "&interval=1d&events=history&includeAdjustedClose=true";
        try {
            URL url = new URL(stockURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            reader.readLine();
        }
        catch(Exception e)
        {
        }

        //Create Data files
        data = new ArrayList<Data>();
        try{
            String s;
            while ((s=reader.readLine()) != null)
            {
                data.add(new Data(s.split(","),mode));
            }
        }
        catch (Exception e)
        {
        }
        if (Data.maxCloseValue != 0) {
            ratio = 400.0 / Data.maxCloseValue;
        }
        else
            ratio = 1;
        yearMap = new TreeMap<Integer,Color>();
    }
    public void getValues()
    {
        Scanner scan = new Scanner(System.in);
        mode = "regular";
        ticker = "";
        data = new ArrayList<Data>();
        yearMap = new TreeMap<Integer,Color>();
    }
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        //Draw Graph
        g.translate(200,500);
        g.drawLine(0,0,0,-400);
        g.drawLine(0,0,35*12,0);

        //Draw Title
        Font font = new Font("Courier New", Font.BOLD, 30);
        centerText(0,-500,font,35*12,100,ticker + " Data",g);

        //Label X axis
        font = new Font("Courier New", Font.BOLD, 15);
        g.setFont(font);

        String[] months = {"J","F","M","A","M","J","J","A","S","O","N","D"};
        for (int i = 0; i < 12; i++)
        {
            g.drawLine(35*i, -5,35*i,5);
            g.drawString(months[i], 35*i-6, 20);
        }

        //Center X axis label
        font = new Font("Courier New", Font.BOLD, 30);
        centerText(0,0,font,35*12,100,"Date",g);

        //Label Y axis
        g.setFont(new Font("Courier New", Font.BOLD, 15));
        for (int i = 0; i <= 10; i++)
        {
            g.drawLine(-5,-40*i,5,-40*i);
            double yValue = 1.0*Math.round(40.0*i/400*Data.maxCloseValue*100)/100;
            String value = "$" + String.format("%.2f",yValue);
            value = String.format("%9s",value);
            g.drawString(value,-100,-40*i+5);
        }
        String yAxisName = "Close Values";
        g.setFont(new Font("Courier New", Font.BOLD, 30));
        for (int i = 0; i < yAxisName.length(); i++)
        {
            g.drawString(yAxisName.substring(i,i+1),-150,-350 + 29*i);
        }

        if (!mode.equals("gradient")) {
            //Display close values
            int prevX = -1;
            int prevY = -1;
            for (Data d : data) {
                int day = d.getDay();
                int month = d.getMonth();
                int year = d.getYear();
                int xCoord = (int) Math.round((month - 1) * 35 + 1.0 * day / getDaysInMonth(month) * 35);
                int yCoord = (int) Math.round(-d.getCloseVal() * ratio);
                fillCircle(xCoord, yCoord, 1, g);
                //Change color of line
                if (yearMap.containsKey(year))
                    g.setColor(yearMap.get(year));
                else
                    yearMap.put(year, new Color((int) (Math.random() * 240), (int) (Math.random() * 240), (int) (Math.random() * 240)));

                if (prevX != -1 && Math.abs(prevX - xCoord) <= 5) {
                    g.drawLine(prevX, prevY, xCoord, yCoord);
                }
                prevX = xCoord;
                prevY = yCoord;
            }

            //Draw Key
            g.setColor(Color.BLACK);
            g.drawString("Key:", 35 * 12 + 50, -375);
            g.setFont(new Font("Courier New", Font.BOLD, 15));
            int i = 0;
            for (Map.Entry<Integer, Color> entry : yearMap.entrySet()) {
                int key = entry.getKey();
                Color c = entry.getValue();
                g.setColor(c);
                g.fillRect(35 * 12 + 50, -350 + 25 * i, 15, 15);
                g.drawString(key + "", 35 * 12 + 70, -335 + 25 * i);
                i++;
            }

            g.setColor(Color.BLACK);
            g.drawRect(35 * 12 + 40, -400, 100, i * 25 + 50);
        }
        else
        {
            TreeMap<String,String> map = new TreeMap<String,String>();
            int prevX = -1;
            int prevY = -1;
            double slope = -1;
            for (Data d : data) {
                int day = d.getDay();
                int month = d.getMonth();
                int xCoord = (int) Math.round((month - 1) * 35 + 1.0 * day / getDaysInMonth(month) * 35);
                int yCoord = (int) Math.round(-d.getCloseVal() * ratio);

                if (prevX != -1)
                {
                    slope = 1.0*(yCoord - prevY) / (1.0 * xCoord - prevX);
                    char ch = (slope >= 0) ? 'p' : 'n';
                    if (map.containsKey(month + "/" + day))
                    {
                        String s = map.get(month + "/" + day);
                        map.put(month + "/" + day, s + ch);
                    }
                    else
                        map.put(month + "/" + day,ch + "");
                }

                prevX = xCoord;
                prevY = yCoord;
            }
            for (Map.Entry<String,String> entry: map.entrySet())
            {
                String str = entry.getValue();
                int denominator = str.length();
                int numerator = denominator - str.replaceAll("p","").length();
                String[] date = entry.getKey().split("/");
                g.setColor(new Color((int)((1-1.0*numerator/denominator)*255),(int)(1.0*numerator/denominator*255),0));
                int day = Integer.parseInt(date[1]);
                int month = Integer.parseInt(date[0]);
                int xCoord = (int) Math.round((month - 1) * 35 + 1.0 * day / getDaysInMonth(month) * 35);
                g.drawLine(xCoord,0,xCoord,-400);
            }
        }
    }
    public void centerText(int x, int y, Font font, int width, int height, String s, Graphics g)
    {
        g.setFont(font);
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = font.getStringBounds(s, frc);
        double rWidth = r2D.getWidth();
        double rHeight = r2D.getHeight();
        double rX = r2D.getX();
        double rY = r2D.getY();

        int a = (int)Math.round((1.0 * width / 2) - (rWidth / 2) - rX);
        int c = (int)Math.round((1.0 * height / 2) - (rHeight / 2) - rY);
        g.drawString(s, x + a, y + c);
    }
    public int getDaysInMonth(int month)
    {
        return switch (month) {
            case 1, 12, 10, 8, 7, 5, 3 -> 31;
            case 2 -> 29;
            case 4, 11, 9, 6 -> 30;
            default -> -1;
        };
    }
    public void fillCircle(int x,int y,int radius, Graphics g)
    {
        g.fillOval(x-radius,y-radius,radius*2,radius*2);
    }

    public static void main(String[] args)
    {
        StockAnalyzer t = new StockAnalyzer();
        JFrame jf = new JFrame();
        jf.setTitle("StockAnalyzer");
        jf.setSize(800,700);
        t.setBackground(Color.WHITE);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(t);
        jf.pack();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 700);
    }
}
