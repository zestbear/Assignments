/*
 * Copyright (c) 2000, 2001, 2002, 2003, 2004, 2005, 2008, 2009
 *	The President and Fellows of Harvard College.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE UNIVERSITY AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

/*
 * Synchronization test code.
 */

#include <types.h>
#include <lib.h>
#include <clock.h>
#include <thread.h>
#include <synch.h>
#include <test.h>

#define NSEMLOOPS     63
#define NLOCKLOOPS    120
#define NCVLOOPS      5
#define NTHREADS      32

static volatile unsigned long testval1;
static volatile unsigned long testval2;
static volatile unsigned long testval3;
static struct semaphore *testsem;
static struct lock *testlock;
static struct cv *testcv;
static struct semaphore *donesem;

static struct semaphore *NW;
static struct semaphore *NE;
static struct semaphore *SW;
static struct semaphore *SE;
static struct semaphore *intersection;
void insertq(long num, int r);
void deleteq(int r)
int traffic[4][2] = { {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1} };
int callmessage = 0;

static void inititems(void) {
	if (testsem == NULL) {
		testsem = sem_create("testsem", 2);
		if (testsem == NULL) {
			panic("synchtest: sem_create failed\n");
		}
	}
    
	if (testlock == NULL) {
		testlock = lock_create("testlock");
		if (testlock == NULL) {
			panic("synchtest: lock_create failed\n");
		}
	}
    
	if (testcv == NULL) {
		testcv = cv_create("testlock");
		if (testcv == NULL) {
			panic("synchtest: cv_create failed\n");
		}
	}
    
	if (donesem == NULL) {
		donesem = sem_create("donesem", 0);
		if (donesem == NULL) {
			panic("synchtest: sem_create failed\n");
		}
	}

	if (NW == NULL) {
        NW = sem_create("NW", 1);
		if (NW == NULL) {
			panic("synchtest: sem_create failed\n");
		}
	}
    
	if (NE == NULL) {
        NE = sem_create("NE", 1);
        if (NE == NULL) {
            panic("synchtest: sem_create failed\n");
        }
    }
    
	if (SW == NULL) {
        SW = sem_create("SW", 1);
        if (SW == NULL) {
            panic("synchtest: sem_create failed\n");
        }
    }
    
	if (SE == NULL) {
        SE = sem_create("SE", 1);
        if (SE == NULL) {
            panic("synchtest: sem_create failed\n");
        }
    }
    
	if(intersection == NULL) {
		intersection = sem_create("intersection", 3);
		if (intersection == NULL) {
			panic("synchtest: sem_create failed\n");
		}
	}
}

char numtodir(int input) {
	if(input == 0) return "N";
	else if(input == 1) return "E";
	else if(input == 2) return "S";
	else return "W";
}

int dirtonum(char input) {
	if(strcmp(input, "N") == 0) return 0;
	else if(strcmp(input, "E") == 0) return 1;
	else if(strcmp(input, "S") == 0) return 2;
	else return 3;
}

void insertq(int num, int r){
    if(traffic[r][0]==-1){
    	traffic[r][0]=num;
    }
    else if(traffic[r][0]!=-1){
        traffic[r][1]=num;
    }
}

void deleteq(int r){
    traffic[r][0]=traffic[r][1];
    traffic[r][1]=-1;
}

static void message_call(int thread_num, char node, int dir) {
	char str[15];

	if(dir == 0) 
		strcpy(str, "go straight");
	else if(dir == 1) 
		strcpy(str, "turn right");
	else if(dir == 2) 
		strcpy(str, "turn left");
	else
		strcpy(str, "error");

	P(testsem);
	kprintf("Thread %2d: from %c will %s\n", thread_num, node, str);
	V(donesem);
}

static void message_function(int thread_num, const char *node, const char *past, const char *now, const char *dest) {
	if(strcmp(node, past) == 0) {
		P(testsem);
		kprintf("Thread %2d: entered %s\n", thread_num, now);
		V(donesem);
}
	else if(strcmp(now, dest) == 0) {
		P(testsem);
		kprintf("Thread %2d: left the intersection\n", thread_num);
		V(donesem);
	}
	else {
		P(testsem);
		kprintf("Thread %2d: in quadrant %s\n", thread_num, now);
		V(donesem);
	}
}

static void straight_and_leave(int thread_num, const char *node, struct semaphore *road_1, struct semaphore *road_2, const char *dest) {
	
	P(intersection);

	P(road_1);
	message_function(thread_num, node, node, road_1->sem_name, dest);
	insertq(thread_num, dirtonum(node));
	while(traffic[dirtonum(node)][0]!=thread_num) {}
	P(road_2);
	message_function(thread_num, node, road_1->sem_name, road_2->sem_name, dest);
	deleteq(dirtonum(node));
	insertq(thread_num, (dirtonum(node)+3)%4);
	while(traffic[(dirtonum(node)+3)%4][0]!=thread_num) {}
	V(road_1);
	message_function(thread_num, node, road_2->sem_name, dest, dest);
	deleteq((dirtonum(node)+3)%4);
	V(road_2);

	V(intersection);
}

static void gostraight(int thread_num, char node){
	switch(node) {
		case 'N' : 
			straight_and_leave(thread_num, numtodir(0), NW, SW, numtodir(2));
			break;
		case 'E' : 
			straight_and_leave(thread_num, numtodir(1), NE, NW, numtodir(3));
			break;
		case 'S' : 
			straight_and_leave(thread_num, numtodir(2), SE, NE, numtodir(0));
			break;
		case 'W' : 
			straight_and_leave(thread_num, numtodir(3), SW, SE, numtodir(1));
			break;
	}
}

static void left_and_leave(int thread_num, const char *node, struct semaphore *road_1, struct semaphore *road_2, struct semaphore *road_3, const char *dest) {
	
	P(intersection);

	P(road_1);
	message_function(thread_num, node, node, road_1->sem_name, dest);
	insertq(thread_num, dirtonum(node));
	while(traffic[dirtonum(node)][0]!=thread_num) {}
	P(road_2);
	message_function(thread_num, node, road_1->sem_name, road_2->sem_name, dest);
	deleteq((dirtonum(node)));
	V(road_1);
	P(road_3);
	insertq(thread_num, (dirtonum(node)+3)%4);
	while(traffic[(dirtonum(node)+3)%4][0]!=thread_num) {}
	message_function(thread_num, node, road_2->sem_name, road_3->sem_name, dest);
	deleteq((dirtonum(node)+3)%4);
	V(road_2);
	insertq(thread_num, (((dirtonum(node)+3)%4)+3)%4);
	while(traffic[(((dirtonum(node)+3)%4)+3)%4][0]!=thread_num) {}
	message_function(thread_num, node, road_3->sem_name, dest, dest);
	deleteq((((dirtonum(node)+3)%4)+3)%4);
	V(road_3);

	V(intersection);
}

static void turnleft(int thread_num, char node) {
	
	switch(node) {
		case 'N' : 
			left_and_leave(thread_num, numtodir(0), NW, SW, SE, numtodir(1));
			break;
		case 'E' : 
			left_and_leave(thread_num, numtodir(1), NE, NW, SW, numtodir(2));
			break;
		case 'S' : 
			left_and_leave(thread_num, numtodir(2), SE, NE, NW, numtodir(3));
			break;
		case 'W' : 
			left_and_leave(thread_num, numtodir(3), SW, SE, NE, numtodir(0));
			break;	  
	}
}

static void right_and_leave(int thread_num, const char *node, struct semaphore *road, const char *dest) {
	
	P(intersection);

	P(road);
	insertq(thread_num, dirtonum(node));
	while(traffic[dirtonum(node)][0]!=thread_num) {}
	message_function(thread_num, node, node, road->sem_name, dest);
	deleteq((dirtonum(node)));
	message_function(thread_num, node, road->sem_name, dest, dest);
	V(road);

	V(intersection);
}

static void turnright(int thread_num, char node) {
	
	switch(node) {
		case 'N' : 
			right_and_leave(thread_num, numtodir(0), NW, numtodir(2));
			break;
		case 'E' : 
			right_and_leave(thread_num, numtodir(1), NE, numtodir(0));
			break;
		case 'S' : 
			right_and_leave(thread_num, numtodir(2), SE, numtodir(1));
			break;
		case 'W' : 
			right_and_leave(thread_num, numtodir(2), SW, numtodir(2));
			break;	  
	}
}

static void semtestthread(void *junk, unsigned long num) {
	(void)junk;
	/*
	 * Only one of these should print at a time.
	 */
	int road = random() % 4;
	int dir = random() % 3;
	char node;

	if (road == 0) node = 'N';
	else if (road == 1) node = 'E';
	else if (road == 2) node = 'S';
	else if (road == 3) node = 'W';

	message_call(num, node, dir);

	if (dir == 0) {
		callmessage += 3;
		gostraight(num, node);
	}
	else if (dir == 1) {
		callmessage += 4;
		turnleft(num, node);
	}
	else if (dir == 2) {
		callmessage += 2;
		turnright(num, node);	
	}
}

int semtest(int nargs, char **args) {

	int i, result;

	(void)nargs;
	(void)args;

	inititems();
	kprintf("Starting semaphore test...\n");
	kprintf("If this hangs, it's broken: ");
	P(testsem);
	P(testsem);
	kprintf("ok\n");

	for (i=0; i<NTHREADS; i++) {
		result = thread_fork("semtest", NULL, semtestthread, NULL, i);
		if (result) {
			panic("semtest: thread_fork failed: %s\n",
			      strerror(result));
		}
	}
	
	for (i=0; i<NTHREADS; i++) {
		V(testsem);
		P(donesem);
	}

	for(i=0; i<callmessage; i++) {
		V(testsem);
		P(donesem);	
	}	

	/* so we can run it again */
	V(testsem);
	V(testsem);

	kprintf("Semaphore test done.\n");
	return 0;
}

static
void
fail(unsigned long num, const char *msg)
{
	kprintf("thread %lu: Mismatch on %s\n", num, msg);
	kprintf("Test failed\n");

	lock_release(testlock);

	V(donesem);
	thread_exit();
}

static
void
locktestthread(void *junk, unsigned long num)
{
	int i;
	(void)junk;

	for (i=0; i<NLOCKLOOPS; i++) {
		lock_acquire(testlock);
		testval1 = num;
		testval2 = num*num;
		testval3 = num%3;

		if (testval2 != testval1*testval1) {
			fail(num, "testval2/testval1");
		}

		if (testval2%3 != (testval3*testval3)%3) {
			fail(num, "testval2/testval3");
		}

		if (testval3 != testval1%3) {
			fail(num, "testval3/testval1");
		}

		if (testval1 != num) {
			fail(num, "testval1/num");
		}

		if (testval2 != num*num) {
			fail(num, "testval2/num");
		}

		if (testval3 != num%3) {
			fail(num, "testval3/num");
		}

		lock_release(testlock);
	}
	V(donesem);
}


int
locktest(int nargs, char **args)
{
	int i, result;

	(void)nargs;
	(void)args;

	inititems();
	kprintf("Starting lock test...\n");

	for (i=0; i<NTHREADS; i++) {
		result = thread_fork("synchtest", NULL, locktestthread,
				     NULL, i);
		if (result) {
			panic("locktest: thread_fork failed: %s\n",
			      strerror(result));
		}
	}
	for (i=0; i<NTHREADS; i++) {
		P(donesem);
	}

	kprintf("Lock test done.\n");

	return 0;
}

static
void
cvtestthread(void *junk, unsigned long num)
{
	int i;
	volatile int j;
	struct timespec ts1, ts2;

	(void)junk;

	for (i=0; i<NCVLOOPS; i++) {
		lock_acquire(testlock);
		while (testval1 != num) {
			gettime(&ts1);
			cv_wait(testcv, testlock);
			gettime(&ts2);

			/* ts2 -= ts1 */
			timespec_sub(&ts2, &ts1, &ts2);

			/* Require at least 2000 cpu cycles (we're 25mhz) */
			if (ts2.tv_sec == 0 && ts2.tv_nsec < 40*2000) {
				kprintf("cv_wait took only %u ns\n",
					ts2.tv_nsec);
				kprintf("That's too fast... you must be "
					"busy-looping\n");
				V(donesem);
				thread_exit();
			}

		}
		kprintf("Thread %lu\n", num);
		testval1 = (testval1 + NTHREADS - 1)%NTHREADS;

		/*
		 * loop a little while to make sure we can measure the
		 * time waiting on the cv.
		 */
		for (j=0; j<3000; j++);

		cv_broadcast(testcv, testlock);
		lock_release(testlock);
	}
	V(donesem);
}

int
cvtest(int nargs, char **args)
{

	int i, result;

	(void)nargs;
	(void)args;

	inititems();
	kprintf("Starting CV test...\n");
	kprintf("Threads should print out in reverse order.\n");

	testval1 = NTHREADS-1;

	for (i=0; i<NTHREADS; i++) {
		result = thread_fork("synchtest", NULL, cvtestthread, NULL, i);
		if (result) {
			panic("cvtest: thread_fork failed: %s\n",
			      strerror(result));
		}
	}
	for (i=0; i<NTHREADS; i++) {
		P(donesem);
	}

	kprintf("CV test done\n");

	return 0;
}

////////////////////////////////////////////////////////////

/*
 * Try to find out if going to sleep is really atomic.
 *
 * What we'll do is rotate through NCVS lock/CV pairs, with one thread
 * sleeping and the other waking it up. If we miss a wakeup, the sleep
 * thread won't go around enough times.
 */

#define NCVS 250
#define NLOOPS 40
static struct cv *testcvs[NCVS];
static struct lock *testlocks[NCVS];
static struct semaphore *gatesem;
static struct semaphore *exitsem;

static
void
sleepthread(void *junk1, unsigned long junk2)
{
	unsigned i, j;

	(void)junk1;
	(void)junk2;

	for (j=0; j<NLOOPS; j++) {
		for (i=0; i<NCVS; i++) {
			lock_acquire(testlocks[i]);
			V(gatesem);
			cv_wait(testcvs[i], testlocks[i]);
			lock_release(testlocks[i]);
		}
		kprintf("sleepthread: %u\n", j);
	}
	V(exitsem);
}

static
void
wakethread(void *junk1, unsigned long junk2)
{
	unsigned i, j;

	(void)junk1;
	(void)junk2;

	for (j=0; j<NLOOPS; j++) {
		for (i=0; i<NCVS; i++) {
			P(gatesem);
			lock_acquire(testlocks[i]);
			cv_signal(testcvs[i], testlocks[i]);
			lock_release(testlocks[i]);
		}
		kprintf("wakethread: %u\n", j);
	}
	V(exitsem);
}

int
cvtest2(int nargs, char **args)
{
	unsigned i;
	int result;

	(void)nargs;
	(void)args;

	for (i=0; i<NCVS; i++) {
		testlocks[i] = lock_create("cvtest2 lock");
		testcvs[i] = cv_create("cvtest2 cv");
	}
	gatesem = sem_create("gatesem", 0);
	exitsem = sem_create("exitsem", 0);

	kprintf("cvtest2...\n");

	result = thread_fork("cvtest2", NULL, sleepthread, NULL, 0);
	if (result) {
		panic("cvtest2: thread_fork failed\n");
	}
	result = thread_fork("cvtest2", NULL, wakethread, NULL, 0);
	if (result) {
		panic("cvtest2: thread_fork failed\n");
	}

	P(exitsem);
	P(exitsem);

	sem_destroy(exitsem);
	sem_destroy(gatesem);
	exitsem = gatesem = NULL;
	for (i=0; i<NCVS; i++) {
		lock_destroy(testlocks[i]);
		cv_destroy(testcvs[i]);
		testlocks[i] = NULL;
		testcvs[i] = NULL;
	}

	kprintf("cvtest2 done\n");
	return 0;
}
