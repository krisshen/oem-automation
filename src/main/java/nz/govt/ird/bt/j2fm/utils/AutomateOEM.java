package nz.govt.ird.bt.j2fm.utils;

import org.apache.commons.io.FileUtils;
import org.ho.yaml.Yaml;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;

class AutomateOEM {

    //click interval time set to 5 mins, to make sure OEM session wont timeout
    static int clickIntervalTime = 300 * 1000;
    static WebDriver webDriver;
    static WebDriverWait webDriverWait;

    static boolean isAllLinkClicked = false;
    static boolean is2HoursLinkClicked = false;

    private static Map<String, Object> loadConfig() {
        File inputFile = new File(System.getProperty("user.dir") + File.separator + "src/main/resources/config.yaml");
        try {
            return (Map<String, Object>) Yaml.load(inputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws InterruptedException {

        Map<String, Object> config = loadConfig();

        //initialize
        FirefoxBinary firefoxBinary = new FirefoxBinary(new File((String) config.get("firefoxpath")));
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        webDriver = new FirefoxDriver(firefoxBinary, firefoxProfile);
        webDriverWait = new WebDriverWait(webDriver, 20);

        //open website
        webDriver.get((String) ((Map)config.get("OEM")).get("url"));

        //login page > username and password
        webDriver.findElement(By.id("t_username::content")).sendKeys((String) ((Map)config.get("OEM")).get("username"));
        webDriver.findElement(By.id("t_password::content")).sendKeys((String) ((Map)config.get("OEM")).get("password"));
        webDriver.findElement(By.id("emasLogin6")).click();

        //waiting for main page loading done
        webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("emTemplate:ch_ln")));

        //click 'WebLogic Domain'
        findAndClick("/html/body/div[2]/div/form/div/div[2]/div/div[5]/div/div[1]/div[2]/div/div[1]/div/div[5]/div/div[1]/div[2]/div/div/div/div[1]/table/tbody/tr[4]/td/div/span[1]/a");

        //click 'eai-portal'
        findAndClick("//*[@title=\"/Farm_eai-portal/eai-portal\"]");

        //click 'soa_server1'
        findAndClick("/html/body/div[2]/div/form/div[2]/div[2]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div/div/div/div/div/div/table/tbody/tr[1]/td/table/tbody/tr/td[1]/div/div/div/div[2]/div/div/div[2]/div/div[3]/div/div[2]/table/tbody/tr[2]/td[1]/span/a[2]");

        //click 'WebLogic Server'
        findAndClick("//td[a=\"WebLogic Server\"]");

        //click 'Performance Summary'
        findAndClick("//tr[td=\"Performance Summary\"]");

        //maximize window
        webDriver.manage().window().maximize();

        //wait for 'keepAliveTime' seconds, it does screenshots every 'screenShotInterval' seconds
        keepOEMAlive((Integer) config.get("keepAliveTime")*1000, (Integer) config.get("screenShotInterval")*1000);

        webDriver.close();
    }

    /**
     * keep OEM session alive and
     * send out screenshots to your email box
     * @param keepAliveTime
     * @param screenShotInterval
     */
    private static void keepOEMAlive(int keepAliveTime, int screenShotInterval) {

        int aliveTimeMS = 0;
        File screenshotFile;

        //click a whatever element every clickIntervalTime ms
        while (aliveTimeMS <= keepAliveTime) {

            //if session keeps alive for 20mins, try to click 'All'
            if (!isAllLinkClicked && aliveTimeMS >= 20 * 60 * 1000) {
                findAndClick("//*[@id=\"emTemplate:timeSelector:allLink\"]");
                isAllLinkClicked = true;
                System.out.println("'All' link is clicked....");
            }
            //if session keeps alive for 1 hour, try to click '2 hours'
            if (!is2HoursLinkClicked && aliveTimeMS >= 60 * 60 * 1000) {
                findAndClick("//*[@id=\"emTemplate:timeSelector:twoHoursLink\"]");
                is2HoursLinkClicked = true;
                System.out.println("'2 hours' link is clicked....");
            }

            sleepFor(clickIntervalTime);

            //just click somewhere...
            webDriver.findElement(By.xpath("/html/body/div[2]/div/form/div[2]/div[2]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div[5]/div/div[1]/div[1]/div/div[2]/table/tbody/tr/td[3]/table/tbody/tr/td[3]/table/tbody/tr/td[1]/span")).click();
            aliveTimeMS += clickIntervalTime;

            //take screenshot if time 'screenShotInterval' past
            if (aliveTimeMS % screenShotInterval == 0) {
                File scrFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
                try {
                    screenshotFile = new File(System.getProperty("user.dir") + "/screenshot_" + getDateTime() + ".png");
                    FileUtils.copyFile(scrFile, screenshotFile);
                    //send email
                    if ((Boolean)loadConfig().get("emailNotification")) {
                        sendMail(screenshotFile);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));
        return dateFormat.format(cal.getTime());
    }

    private static void sendMail(File attachment) {

        // Recipient's email ID needs to be mentioned.
        String to = loadConfig().get("emailAddress").toString();

        // Sender's email ID needs to be mentioned
        String from = "Automation Agent <Kris.Shen@ird.govt.nz>";


        // Assuming you are sending email from localhost
        String host = "sthcas.ed.ird.govt.nz";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("OEM Status -> " + attachment.getName());

            // Now set the actual message
//            message.setText("no wonder!");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText("");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(attachment);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(attachment.getName());
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart );

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    private static void sleepFor(int t) {
        try {
            Thread.sleep(t);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * click one element after it is found by selenium
     * @param xpath
     */
    private static void findAndClick(String xpath) {
        webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
        webDriver.findElement(By.xpath(xpath)).click();
    }
}
