package parallelzo.min;
import java.util.concurrent.RecursiveTask;

/**
 * MinArrayTask is a task for {@link java.util.concurrent.ForkJoinPool ForkJoinPool}
 * that searches for the smallest value in a generic array, and returns it. <br />
 * The search is parallel if the array length is greater then 10,000 elements. <br />
 * Recommended use: when array length is greater then 500,000 elements. 
 * 
 * @author Oz Elentok <oz.elen@gmail.com>
 * @version 1.0
 */
class MinArrayTask<T extends Comparable<T>> extends RecursiveTask<T> {
		private static final long serialVersionUID = 1L;
		/**
		 * Minimum length of array to make new tasks
		 */
		private static final int MINLEN = 10000;
		/**
		 * The array to search in
		 */
		private final T[] array;
		/**
		 * Starting index of searching range
		 */
		private final int start;
		/**
		 * Ending index of searching range
		 */
		private final int end;
		
		/**
		 * Creates a searching task for the smallest value in a generic array. <br />
		 * Searches the array from <code>start</code> up to <code>end</code>.
		 * @param array		The Array to sort	
		 * @param start		Starting index of searching range
		 * @param end		Ending index of searching range
		 */
		public MinArrayTask(T[] array, int start, int end) {
			this.array = array;
			this.start = start;
			this.end = end;
		}
		/**
		 * Creates a searching task for the smallest value in a generic array.
		 * @param array		The Array to sort	
		 */
		public MinArrayTask(T[] array) {
			this.array = array;
			this.start = 0;
			this.end = array.length - 1;
		}
		/**
		 * Searches for the smallest value in a generic array. <br />
		 * the length of the search range decides whether to make new tasks(adding them to the thread pool),
		 * or to search in them on the current thread.
		 * @return	Smallest element between <code>start</code> and </end> in the array 
		 */
		public T min() {
			int len = end - start + 1;
			if(len < MINLEN) {
				T min = array[start];
				for(int i = start + 1; i <= end; i++) {
					if(min.compareTo(array[i]) > 0) {
						min = array[i];
					}
				}
				return min;
			}
			else {
				int mid = (start + end) / 2;
				T leftMin, rightMin;
				MinArrayTask<T> left = new MinArrayTask<T>(array, start, mid);
				MinArrayTask<T> right = new MinArrayTask<T>(array, mid + 1, end);
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
		protected T compute() {
			return min();
		}
	}
