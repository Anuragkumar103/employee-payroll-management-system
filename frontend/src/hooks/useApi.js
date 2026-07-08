import { useCallback, useState } from 'react';

/**
 * Small helper hook to reduce load/error boilerplate around API calls.
 * Usage: const { run, loading, error } = useApi();
 *        const data = await run(() => employeeService.getAll());
 */
export function useApi() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const run = useCallback(async (fn) => {
    setLoading(true);
    setError(null);
    try {
      const response = await fn();
      return response.data?.data ?? response.data;
    } catch (err) {
      const message = err.response?.data?.message || err.message || 'Something went wrong';
      setError(message);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return { run, loading, error };
}
