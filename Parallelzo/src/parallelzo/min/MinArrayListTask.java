package parallelzo.min;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

/**
 * MinArrayListTask is a task for {@link java.util.concurrent.ForkJoinPool ForkJoinPool}
 * that searches for the smallest value in a generic ArrayList, and returns it. <br />
 * The search is parallel if the array length is greater then 10,000 elements. <br />
 * Recommended use: when array length is greater then 500,000 elements. 
 * 
 * @author Oz Elentok <oz.elen@gmail.com>
 * @version 1.0
 */
class MinArrayListTask<T extends Comparable<T>> extends RecursiveTask<T> {
		private static final long serialVersionUID = 1L;
		/**
		 * Minimum length of array to make new tasks
		 */
		private static final int MINLEN = 10000;
		/**
		 * The array to search in
		 */
		private final ArrayList<T> array;
		/**
		 * Starting index of searching range
		 */
		private final int start;
		/**
		 * Ending index of searching range
		 */
		private final int end;
		
		/**
		 * Creates a searching task for the smallest value in a generic ArrayList. <br />
		 * Searches the array from <code>start</code> up to <code>end</code>.
		 * @param array		The ArrayList to sort	
		 * @param start		Starting index of searching range
		 * @param end		Ending index of searching range
		 */
		public MinArrayListTask(ArrayList<T> array, int start, int end) {
			this.array = array;
			this.start = start;
			this.end = end;
		}
		/**
		 * Creates a searching task for the smallest value in a generic ArrayList.
		 * @param array		The ArrayList to sort	
		 */
		public MinArrayListTask(ArrayList<T> array) {
			this.array = array;
			this.start = 0;
			this.end = array.size() - 1;
		}
		/**
		 * Searches for the smallest value in a generic ArrayList. <br />
		 * the length of the search range decides whether to make new tasks(adding them to the thread pool),
		 * or to search in them on the current thread.
		 * @return	Smallest element between <code>start</code> and </end> in the array 
		 */
		public T min() {
			int len = end - start + 1;
			if(len < MINLEN) {
				T min = array.get(start);
				for(int i = start + 1; i <= end; i++) {
					if(min.compareTo(array.get(i)) > 0) {
						min = array.get(i);
					}
				}
				return min;
			}
			else {
				int mid = (start + end) / 2;
				T leftMin, rightMin;
				MinArrayListTask<T> left = new MinArrayListTask<T>(array, start, mid);
				MinArrayListTask<T> right = new MinArrayListTask<T>(array, mid + 1, end);
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
