package nz.govt.ird.bt.j2fm.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

class AutomateOEM {

    static String url = "URL";

    public static void main(String[] args) throws InterruptedException {



        File pathBinary = new File("C:\\Kris\\Tools\\Mozilla Firefox 36\\firefox.exe");

        FirefoxBinary firefoxBinary = new FirefoxBinary(pathBinary);
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        WebDriver webDriver = new FirefoxDriver(firefoxBinary, firefoxProfile);
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, 20);

        webDriver.get(url);
        //login page > username and password
        webDriver.findElement(By.id("t_username::content")).sendKeys("USERNAME");
        webDriver.findElement(By.id("t_password::content")).sendKeys("PASSWORD");
        webDriver.findElement(By.id("emasLogin6")).click();
        //waiting for main page loading done
        webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("emTemplate:ch_ln")));
        //click 'WebLogic Domain'
        findAndClick(webDriver, webDriverWait, "/html/body/div[2]/div/form/div/div[2]/div/div[5]/div/div[1]/div[2]/div/div[1]/div/div[5]/div/div[1]/div[2]/div/div/div/div[1]/table/tbody/tr[4]/td/div/span[1]/a");
        //click 'eai-portal'
        findAndClick(webDriver, webDriverWait, "//*[@title=\"/Farm_eai-portal/eai-portal\"]");
        //click 'soa_cluster'
//        findAndClick(webDriver, webDriverWait, "/html/body/div[2]/div/form/div/div[2]/div/div[5]/div/div[1]/div[2]/div/div[1]/div/div[5]/div/div[1]/div[2]/div/div/div/div[1]/table[2]/tbody/tr[2]/td/div/span[1]/a");
        //click 'soa_server1'
        findAndClick(webDriver, webDriverWait, "/html/body/div[2]/div/form/div[2]/div[2]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div/div/div/div/div/div/table/tbody/tr[1]/td/table/tbody/tr/td[1]/div/div/div/div[2]/div/div/div[2]/div/div[3]/div/div[2]/table/tbody/tr[2]/td[1]/span/a[2]");
        //waiting for soa_server1 page loading done
//        webDriverWait.until((ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[2]/div/form/div[2]/div[2]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div/div/div/div/div/div/table/tbody/tr[1]/td/table/tbody/tr/td[2]/div/div/div/div/div/div/table[1]/tbody/tr/td[2]/table/tbody/tr/td[3]/table/tbody/tr/td/h2"))));
        //click 'WebLogic Server'
//        findAndClick(webDriver, webDriverWait, "/html/body/div[2]/div/form/div[2]/div[2]/div/div[5]/div/div[1]/div[2]/div/div[3]/div/div[5]/div/div[1]/div[1]/div/div[3]/table/tbody/tr/td[2]/table/tbody/tr/td/div/div[1]/div[1]/table/tbody/tr/td/div/div/table/tbody/tr/td[2]/a");
        findAndClick(webDriver, webDriverWait, "//td[a=\"WebLogic Server\"]");
        //click 'Performance Summary'
        findAndClick(webDriver, webDriverWait, "//tr[td=\"Performance Summary\"]");

        webDriver.manage().window().maximize();

        //wait for 1 hour
//        webDriver.close();

    }

    /**
     * click one element after it is found by selenium
     * @param webDriver
     * @param webDriverWait
     * @param xpath
     */
    private static void findAndClick(WebDriver webDriver, WebDriverWait webDriverWait, String xpath) {
        webDriverWait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xpath)));
        webDriver.findElement(By.xpath(xpath)).click();
    }
}
