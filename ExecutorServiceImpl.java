//* DO NOT ALTER OR REMOVE ANYTHING FROM THIS FILE.

/*@Author - Suraj Yadav
  Executor service working and implementation in java based apps
*/

package javaexecutorservice;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/*  
 * The executor service mechanism in java can be used to perform tasks concurrently without blocking the application's main thread 
 * thus providing a performance boost to our app. 
 * 
 *  Inorder to avoid new thread creation for every new task assigned , Executor service provide us an abstraction layer over that 
 *  and creates a pool of certain number of threads 
 * 
 *  It is used to perform Async operations using a different thread taken from thread pool
 *   
 *   The idea behind executor service is simple -> 
 *   ------------------------------------------
 *
 *    	a).A working thread T1 is executing some task and the task has diff operations {operations 1, operation 2 , .... etc}
 *    	b).and lets say anyone of the operation can be performed independently (Ideal usecase for executor service)
 *    	c).then this particluar operation {say operation X} can be submitted to executor service.
 *    	d).Executor service then picks up a thread from its thread pool {Created at the time of executor instantiaton} and assign that task to it.
 *    	e).So , now the ->  Operation X is performed by Thread -> Worker-Pool thread without interference to the main working thread T1.
 *      f).The assigned task will now be executed concurrently on the same / different core of the m/c
 * 
 *  The tasks submitted to an executor services are held inside a @link{LinkedBlockingQueue} through which the tasks are assigned to free threads
 *  If none of the threads in Pool are free then the submitted tasks will wait until a thread becomes free 
 *  
 *  The @link{ExecutorService} has 2 implementations :
 *     1.ThreadPoolExecutor
 *     2.ScheduledThreadPoolExecutor
 *  
 *  There are different ways through which an ExecutorService can delegate the submitted tasks to the worker threads : 
 *  
 *   	 execute(Runnable)
 *		 submit(Runnable)
 *		 submit(Callable)
 *		 invokeAny(...) -> not covered 
 *		 invokeAll(...) -> not covered
 *  
 *  
 *   Runnable VS Callable : 	
 *   																		 |	
 *     Runnable tasks can be performed concurrently by a thread              |     Callable can only be executed by an executor service 
 *     or an executor service                                                |     
 *     																		 |	
 *     return type is void 													 |	   return type is generic 		   		 
 *     																		 |	
 *     If the submitted task does not return any result then Runnable        |     If the submitted task has a result as an O/P then callable 
 *     is used																 |	   should be used. 	
 *     																		 |
 *     																		 |
 *     	
 * 
 *  What problems are solved using Executor service in applications ? 
 *  
 *  ->  Easy way for developers to work with muthithreading model 
 *  ->  performance boost in application
 *  ->  Resource pooling is done in Executor service by reusing the worker threads for new incoming request
 *  ->  Better CPU utilization 
 *  ->  Very Important Api for low latency apps  
 *  
 *  References -> @link{Executors} 
 *  			  @link{ExecutorService}
 *  			  @link{ThreadPoolExecutor}
 *  
 */


public class ExecutorServiceImpl {

	/*
	 * Executor service instance creation using thread pool size == 1 
	 * Thread pool will contain single thread for every task submitted 
	 * 
	 * Thus all the tasks are performed sequentially  
	 */
	
	private static ExecutorService executorService1 = Executors.newSingleThreadExecutor();
	
	/*
	 * Executor service instance creation using fixed thread pool size Thread
	 * pool will contain 2 threads for task delegation as provided
	 */
	
	private static ExecutorService executorService2 = Executors.newFixedThreadPool(2);
	
	
	private static ExecutorService executorService3 = Executors.newScheduledThreadPool(2);
	
	/*
	 * Executor service can also work with virtual thread concept introduced in java 19
	 * 
	 * ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
	 */

	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
	     
		System.out.println("Thread Info : " + Thread.currentThread().getName());
	    
	//	taskUsingExecuteMethod(); 
	//	taskUsingSubmitMethod();
	//	taskUsingSubmitMethod1();
		
	}

	private static void taskUsingExecuteMethod() {

		/* 
		 * @performTaskUsingExecutorService was invoked by main Thread but the task
		 * submitted to executor service was perfomed on a different thread
		 * 
		 * The execute method uses @link{Runnable} as argument and performs the task asynchronously   
		 */
		
		System.out.println("Thread Info : " + Thread.currentThread().getName());
		executorService2.execute(() -> System.out.println("Thread Info : " + Thread.currentThread().getName()));
	//	executorService.shutdown();

	}
	
	
	// @Runnable as argument 
	private static void taskUsingSubmitMethod() throws InterruptedException, ExecutionException {
		
		System.out.println("Caller Thread Info : " + Thread.currentThread().getName());
		
		/*
		 * A @link{Runnable} task is submitted to the executor Service using #submit method and 
		 * a Future<?> object is returned representing the submitted task 
		 * submit call is used when we are dependent on the operation performed 
		 */
		
		Future<?> submit = executorService2.submit(() -> compute());
		
		/*
		 * here #get() method is a blocking call which blocks the main thread
		 * until the result of future is returned thus defeats the purpose of
		 * Asyncronous mechanism using executors 
		 * 
		 * Thus #get() call must be used carefully
		 */
		
		System.out.println(submit.get());  // if #get() returns null means the task is completed 
	
	}

	
	  // @Callable as argument
      private static void taskUsingSubmitMethod1() throws InterruptedException, ExecutionException {
		
		System.out.println("Caller Thread Info : " + Thread.currentThread().getName());
		
		/*
		 * A @link{Callable} task is submitted to the executor Service using #submit method and 
		 * a Future<?> object is returned representing the submitted task 
		 * submit call is used when we are dependent on the operation performed 
		 */
		
		Callable<String> callable = () -> "Result of Callable";
		Future<?> submit = executorService2.submit(callable);
		
		/*
		 * here #get() method is a blocking call which blocks the main thread
		 * until the result of future is returned thus defeats the purpose of
		 * Asyncronous mechanism using executors 
		 * 
		 * Thus #get() call must be used carefully
		 */
		
		System.out.println(submit.get());  // if #get() returns null means the task is completed 
	
	}
	
	
	
	/*
	 * assuming it to be a high computation task where 
	 * thread waits for  @10000 seconds
	 */
	private static void compute() {
		
		try {
			System.out.println("Thread : " + Thread.currentThread().getName() + " going to sleep");
			Thread.sleep(10000);
			System.out.println("Thread : " + Thread.currentThread().getName() + " out of sleep");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	
	}

 