package parallelzo.min;
import java.util.concurrent.RecursiveTask;

/**
 * MinIntTask is a task for {@link java.util.concurrent.ForkJoinPool ForkJoinPool}
 * that searches for the smallest value in an int Array, and returns it. <br />
 * The search is parallel if the array length is greater then 10,000 elements. <br />
 * Recommended use: when array length is greater then 500,000 elements. 
 * 
 * @author Oz Elentok <oz.elen@gmail.com>
 * @version 1.0
 */
class MinIntTask extends RecursiveTask<Integer> {
		private static final long serialVersionUID = 1L;
		/**
		 * Minimum length of array to make new tasks
		 */
		private static final int MINLEN = 10000;
		/**
		 * The array to search in
		 */
		private final int[] array;
		/**
		 * Starting index of searching range
		 */
		private final int start;
		/**
		 * Ending index of searching range
		 */
		private final int end;
		
		/**
		 * Creates a searching task for the smallest value in an int array. <br />
		 * Searches the array from <code>start</code> up to <code>end</code>.
		 * @param array		The Array to sort	
		 * @param start		Starting index of searching range
		 * @param end		Ending index of searching range
		 */
		public MinIntTask(int[] array, int start, int end) {
			this.array = array;
			this.start = start;
			this.end = end;
		}
		/**
		 * Creates a searching task for the smallest value in an int array.
		 * @param array		The Array to sort	
		 */
		public MinIntTask(int[] array) {
			this.array = array;
			this.start = 0;
			this.end = array.length - 1;
		}
		/**
		 * Searches for the smallest value in an int array. <br />
		 * the length of the search range decides whether to make new tasks(adding them to the thread pool),
		 * or to search in them on the current thread.
		 * @return	Smallest element between <code>start</code> and </end> in the array 
		 */
		public Integer min() {
			int len = end - start + 1;
			if(len < MINLEN) {
				int min = array[start];
				for(int i = start + 1; i <= end; i++) {
					if(min > array[i]) {
						min = array[i];
					}
				}
				return new Integer(min);
			}
			else {
				int mid = (start + end) / 2;
				Integer leftMin, rightMin;
				MinIntTask left = new MinIntTask(array, start, mid);
				MinIntTask right = new MinIntTask(array, mid + 1, end);
				left.fork();
				rightMin = right.compute();
				leftMin = left.join();
				return (leftMin.compareTo(rightMin) <= 0) ? leftMin : rightMin;
			}
		}
		/**
		 * Starts the search process.
		 * @return Smallest element between <code>start</code> and </end> in the array
		 */
		@Override
		protected Integer compute() {
			return min();
		}
	}
