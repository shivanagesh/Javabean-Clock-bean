// Clock Bean Class
// Clock.java
package Clock;

/**
 *
 * @author Shivanagesh Chandra <schandra@scu.edu>
 * Added some parts of this bean program.
 */
// Imports
import java.awt.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Clock extends Canvas implements Serializable, Runnable
{
    // state & properties

    /**
     * Transient variables are not part of the persistent state of an object.
     * These variable can't be Serialized.
     */
    private transient Image offImage;
    private transient Graphics offGrfx;
    private transient Thread clockThread;
    private boolean raised;     //This property is used for 3-D effect around bean's border. 
    private boolean digital;    //This property is used to switch between Digital mode and Analog mode.

    // ******* Constructors  *********** //
    //Default Constructor
    public Clock()
    {
        //Call constructor Clock(boolean r, boolean d) to set raised and digital as false.
        this(false, false);
    }
    //End of default constructor

    public Clock(boolean r, boolean d)
    {
        // Allow the superclass constructor to do its thing
        super();

        // Set attributes/properties
        raised = r;
        digital = d;

        //Setting up default size to width=250, height=250
        setSize(250, 250);

        //Setting the background
        setBackground(Color.lightGray);

        //Setting the fore background
        setForeground(Color.red);

        //Creating and starting the thread
        clockThread = new Thread(this);
        clockThread.start();

    }
    // ******* End of constructors  *********** //

    // Accessor methods
    // Check if raised is set to true or false
    public boolean isRaised()
    {
        return raised;
    }

    //End of isRasied() method 

    /**
     * Set raised to true or false which ever value is passed as argument from
     * user and initiates repaint method to update output
    *
     */
    public void setRaised(boolean r)
    {
        raised = r;
        repaint();
    }
    //End  of setRasied() method 

    // This is method will return Digital varable
    public boolean isDigital()
    {
        return digital;
    }
    //End of isDigital() method  

    /**
     * Set digital to true or false which ever value is received as argument
     * from user and initiates repaint method to update output
     */
    public void setDigital(boolean d)
    {
        digital = d;
        repaint();
    }
    //End of setDigital() method 

    //Overriding run method in runnable interface
    @Override
    public void run()
    {
        //To keep clock running.
        while (true) {
            // repaint() method will internally call paint() method
            repaint();

            //Sleep for 1 second, sleep will internally suspend excution and then perform repaint. 1000 mile seconds = 1 sec.  
            try {
                clockThread.currentThread().sleep(1000);
            }// End of try block 
            catch (InterruptedException e) {
                System.out.println("Interrupet Exception: " + e.getMessage());
            }// End of catch block
            catch (Exception e) {
                // you handle the exception here
                System.out.println("Exception: " + e.getMessage());
            }// End of catch block
        }
        //End of while loop.
    }
    //End of run() method

    @Override
    public void update(Graphics g)
    {
        paint(g);
    }
    //End of update() method

    @Override
    public synchronized void paint(Graphics g)
    {
        //Get the size of window. 
        Dimension dim = getSize();

        // Create the offscreen graphics context
        //This graphics object is useed to paint and repaint.
        offImage = createImage(dim.width, dim.height);
        offGrfx = offImage.getGraphics();

        // Painting the background with 3-d effect 
        //Set graphics color same as background color. 
        offGrfx.setColor(getBackground());
        //Fill rectangle in the graphic with width and height with window height and width.
        offGrfx.fillRect(0, 0, dim.width, dim.height);
        //Draw boundary for a 3D rectangle
        offGrfx.draw3DRect(0, 0, dim.width, dim.height, raised);
        //Set color as current foreground color.
        offGrfx.setColor(getForeground());

        // Paint the clock
        // If digital = true, draw digital clock else draw analog clock
        if (digital) {
            drawDigitalClock(offGrfx);
        } //End of if
        else {
            drawAnalogClock(offGrfx);
        }   //End of else

        // Paint the image onto the screen
        g.drawImage(offImage, 0, 0, null);
    }   //End of paint() method

    // Private support methods
    private void drawAnalogClock(Graphics g)
    {

        //Get the size of window. 
        Dimension dim = getSize();

        //Set Font
        g.setFont(getFont());

        //Graphics2D object helps to access graphics functions like Paint,Stroke and Draw.
        Graphics2D ga = (Graphics2D) g;

        //Calculte center point of rectangle, which helps to draw clock hands
        //Set center point as width/2, height/2
        Point center = new Point(dim.width / 2, dim.height / 2);

        //To draw circle height should be equal width, so get smallest value from both. 
        int dia = (dim.width <= dim.height ? dim.width : dim.height);
        int radius = (int) Math.ceil(dia / 2);

        // Calcualte point that need to covered on top left corner
        int x = dim.width >= dim.height ? Math.abs(dim.width - dim.height) / 2 : 0;
        int y = dim.height >= dim.width ? Math.abs(dim.height - dim.width) / 2 : 0;
        Point topLeftCorner = new Point(x, y);

        //Draw the clock shape
        //Draw circular clock with white background
        g.setColor(Color.white);
        //fillOval will draw cicle if both width and height are same 
        g.fillOval(topLeftCorner.x, topLeftCorner.y, dia, dia);

        //Draw circumference/boundary of the clock with black color.
        g.setColor(Color.BLACK);
        g.drawArc(topLeftCorner.x, topLeftCorner.y, dia, dia, 0, 360);

        // Draw the seconds lines and numbers
        for (int linePointCnt = 1; linePointCnt <= 60; linePointCnt++) {
            //Calculate i/5 value. if i/5 = 0, in case of 60, number to be printed is 12.
            int numToPrinted = (linePointCnt / 5 == 0 ? 12 : linePointCnt / 5);

            // Calculate angle in radians for each second/ value of i.
            double angle = (Math.PI / 180) * 6 * linePointCnt;
            // Calculate x an y cordiante for  lines to be printed.
            int startLine = radius - 10;
            int startX = (int) (center.x + ((Math.sin(angle) * startLine)));
            int startY = (int) (center.y - ((Math.cos(angle) * startLine)));
            int endLine = radius - 20;
            if (linePointCnt % 5 == 0) {
                endLine = radius - 15;
            }
            int endX = (int) (center.x + ((Math.sin(angle) * endLine)));
            int endY = (int) (center.y - ((Math.cos(angle) * endLine)));

            //Points where lines will be printed
            Point startLinePosition = new Point(startX, startY);
            Point endLinePosition = new Point(endX, endY);
            // Set the color to blue.
            ga.setColor(Color.blue);
            // Set stroke size as 1 for printed numbers.
            ga.setStroke(new BasicStroke(1));
            g.setFont(new Font("TimesRoman", Font.BOLD, 15));
            //Draw the strokes/lines and digits
            //If the position corresponds to a number other than multiple of 5, then draw bigger strokes 
            //else draw shorter stroke and draw the number
            if (linePointCnt % 5 != 0) {
                ga.setColor(Color.black);
                ga.drawLine(startLinePosition.x, startLinePosition.y, endLinePosition.x, endLinePosition.y);
            } else {
                ga.setColor(Color.black);
                ga.drawLine(startLinePosition.x, startLinePosition.y, endLinePosition.x, endLinePosition.y);
                ga.setColor(Color.blue);
                int stringStartX = (int) (center.x + ((Math.sin(angle) * (radius - 35))));
                int stringStartY = (int) (center.y - ((Math.cos(angle) * (radius - 35))));
                if (numToPrinted <= 12 && numToPrinted >= 10 || numToPrinted <= 3 && numToPrinted >= 1) {
                    stringStartX = stringStartX - 5;
                } else if (numToPrinted <= 9 && numToPrinted >= 6 || numToPrinted <= 6 && numToPrinted >= 3) {
                    stringStartY = stringStartY + 5;
                }
                ga.drawString(Integer.toString(numToPrinted), stringStartX, stringStartY);
                ga.setStroke(new BasicStroke(1));
            }   //End of else
        }   //End of for loop

        //Draw center point, make it drakers
        g.fillOval(center.x - 2, center.y - 2, 5, 5);

        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        double secondsHandAngle = (Math.PI / 180) * 6 * seconds;

        // Calculate end values of x an y cordiante for  seconds line to be printed.
        ga.setColor(Color.red);
        int endLine = radius - 40;
        int endX = (int) (center.x + ((Math.sin(secondsHandAngle) * endLine)));
        int endY = (int) (center.y - ((Math.cos(secondsHandAngle) * endLine)));
        ga.drawLine(center.x, center.y, endX, endY);

        double minutesHandAngle = (Math.PI / 180) * 6 * minutes;
        // Calculate end values of x an y cordiante for  mintues line to be printed.
        ga.setColor(Color.black);
        endLine = radius - 50;
        endX = (int) (center.x + ((Math.sin(minutesHandAngle) * endLine)));
        endY = (int) (center.y - ((Math.cos(minutesHandAngle) * endLine)));
        ga.drawLine(center.x, center.y, endX, endY);

        double hoursHandAngle = (Math.PI / 180) * ((6 * 5 * (hours % 12)) + (minutes * 0.5));
        // Calculate end values of x an y cordiante for  hours line to be printed.
        ga.setColor(Color.black);
        ga.setStroke(new BasicStroke(2));
        endLine = radius - 60;
        endX = (int) (center.x + ((Math.sin(hoursHandAngle) * endLine)));
        endY = (int) (center.y - ((Math.cos(hoursHandAngle) * endLine)));
        ga.drawLine(center.x, center.y, endX, endY);

    }   //End of drawAnalogClock() method

    // Draw digital clock
    private void drawDigitalClock(Graphics g)
    {
        //Get the size of window. 
        Dimension dim = getSize();

        //Graphics2D object helps to access graphics functions like Paint,Stroke and Draw.
        Graphics2D ga = (Graphics2D) g;

        // Setting Font to bigger size
        g.setFont(new Font("TimesRoman", Font.BOLD, 36));
        ga.setColor(Color.BLACK);

        //Get the time
        Date rawTime = Calendar.getInstance().getTime();

        // Setting time formart to hours:minutes:seconds format(AM/PM) 
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");

        //create a new String(time)using the date format from rawDate that we retirved from system calender.
        String time = formatter.format(rawTime);

        //Draw the time into the screen
        ga.drawString(time, dim.width / 8, dim.height / 2);

    }   //End of drawDigitalClock() method
} //End of Clock class 
