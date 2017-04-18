package testcases;

import java.util.Random;

import org.apache.commons.lang3.time.StopWatch;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import util.CommonMethods;
import util.Data;
import util.Selector;
import base.BaseTest;

public class Subscription extends BaseTest {
	
	@BeforeClass
	public void logIn()
	{
		CommonMethods.enter(Selector.customerIdTextbox, Data.activeDataUsername);
		CommonMethods.enter(Selector.passwordTextbox, Data.activeDataPassword);
		CommonMethods.click(Selector.singInButton);
	}
	
	@Test
	public void sixSubscriptionsVerification()
	{
		test = extent.createTest("sixSubscriptionsVerification", "Able to add only 6 subscriptions");
		int initialSubscriptionCount = Integer.parseInt(CommonMethods.getText(Selector.subscriptionCount));
		test.info("Intial Subscription count: "+initialSubscriptionCount);
		Assert.assertTrue(initialSubscriptionCount == 0);
		for(int i=1; i<=6; i++)
		{
			CommonMethods.click(Selector.addSubscriptionButton);
			CommonMethods.waitForElement(Selector.subscriberNameTextbox);
			Random rand= new Random();
			int number=rand.nextInt(10000);
			CommonMethods.enter(Selector.subscriberNameTextbox,"Subscriber"+ number);
			CommonMethods.click(Selector.submitButton);
			CommonMethods.waitForElementInvisible(Selector.submitButton);
			test.info("Added Subscription: "+i);
		}
		int finalSubscriptionCount = Integer.parseInt(CommonMethods.getText(Selector.subscriptionCount));
		Assert.assertTrue(finalSubscriptionCount == 6);
	}
	
	@Test(dependsOnMethods={"sixSubscriptionsVerification"})
	public void seventhSubscriptionVerification()
	{
		test = extent.createTest("seventhSubscriptionVerification", "Can not add 7th subscriber");
		int initialSubscriptionCount = Integer.parseInt(CommonMethods.getText(Selector.subscriptionCount));
		test.info("Intial Subscription count: "+initialSubscriptionCount);
		Assert.assertTrue(initialSubscriptionCount == 6);
		CommonMethods.click(Selector.addSubscriptionButton);
		CommonMethods.waitForElement(Selector.subscriptionExceedErrorMessage);
		String errorMessage = CommonMethods.getText(Selector.subscriptionExceedErrorMessage);
		Assert.assertEquals("Maximum subscriptions limit exceeded", errorMessage.trim());
		test.info("Error displayed for 7th Subscription");
		CommonMethods.click(Selector.subscriptionExceedCloseButton);
		CommonMethods.waitForElementInvisible(Selector.subscriptionExceedCloseButton);
	}
	
	@Test
	public void activeDataUsageVerification() throws InterruptedException
	{
		try
		{
		test = extent.createTest("activeDataUsageVerification", "Download speed within the usage limit");
		
		StopWatch stopWatch = new StopWatch();
		
		CommonMethods.waitForElement(Selector.subsriptionStatus);
		String status = CommonMethods.getText(Selector.subsriptionStatus);
		Assert.assertEquals(status.trim(), "Active");
		test.info("Status is Active");
		
		
		stopWatch.start();
		
		CommonMethods.click(Selector.downloadLink);
		CommonMethods.waitForElement(Selector.logoutButton);
		
		while(!fileExist())
		{
			Thread.sleep(300);
		}
		
		stopWatch.stop();
		
		test.info("File download time is : "+ stopWatch.getTime()+" milli seconds");
				
		Assert.assertTrue(fileExist());
		test.info("File downloaded successfully");
		}
		finally
		{
			fileDelete();
		}
	}
	
	@Test(dependsOnMethods={"activeDataUsageVerification"})
	public void exceedDataUsageVerification() throws InterruptedException
	{
		try{
		test = extent.createTest("exceedDataUsageVerification", "Download speed after usage limit");
		
		StopWatch stopWatch = new StopWatch();
		
		CommonMethods.waitForElement(Selector.subsriptionStatus);
		String status = CommonMethods.getText(Selector.subsriptionStatus);
		Assert.assertEquals(status.trim(), "Active");
		test.info("Status is Active");
			
		stopWatch.start();
		
		CommonMethods.click(Selector.downloadLink);
		CommonMethods.waitForElement(Selector.logoutButton);
		
		while(!fileExist())
		{
			Thread.sleep(300);
		}
		
		stopWatch.stop();
		
		test.info("File download time is : "+ stopWatch.getTime()+ " milli seconds");
		
		Assert.assertTrue(fileExist());
		test.info("File downloaded successfully");
		}
		finally
		{
			fileDelete();
		}
	}
		
	
	@AfterClass
	public void tearDown() throws InterruptedException
	{
		int subscriptionCount = Integer.parseInt(CommonMethods.getText(Selector.subscriptionCount));
		for(int i=1; i<=subscriptionCount; i++)
		{
			CommonMethods.click(Selector.deleteSubscriber);
			Thread.sleep(1000);
		}
		CommonMethods.click(Selector.resetButton);
		CommonMethods.click(Selector.logoutButton);
	}
}
