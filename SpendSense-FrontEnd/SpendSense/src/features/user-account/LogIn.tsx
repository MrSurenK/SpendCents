import styles from "./LogIn.module.css";
import auth from "./Registration.module.css";
import { Link, useNavigate } from "react-router";
import { useState } from "react";
import { useLoginMutation } from "../../redux/rtk-queries/authService.ts";
import { useAppDispatch } from "../../hooks/reduxHooks.ts";
import { loginState, type LoginPayload } from "../../redux/slices/authSlice.ts";

interface LogInForm {
  username: string;
  password: string;
}

function LogIn() {
  //Manage form states
  const [formData, setFormData] = useState<LogInForm>({
    username: "",
    password: "",
  });

  interface BackendError {
    description: string;
  }

  function isBackendError(obj: unknown): obj is BackendError {
    return typeof obj === "object" && obj !== null && "description" in obj && typeof obj.description === "string";
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const { name, value } = e.target;
    // console.log(`${name}:${value}`);
    setFormData((prev) => ({
      ...prev,
      [name as keyof LogInForm]: value,
    }));
  };

  const [login, { error, isLoading }] = useLoginMutation();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      // wait for the login mutation to finish and unwrap the result
      const result = await login(formData).unwrap();
      if (result) {
        // update redux state AFTER login mutation resolves
        const userInfo: LoginPayload = {
          userId: result.userId,
          username: result.username,
          lastLogin: new Date(result.lastLogin).toISOString(),
        };
        dispatch(loginState(userInfo));

        // navigate only after Redux state updated
        navigate("/dashboard");
      }
    } catch (err) {
      console.error("Login failed:", err);
    }
  };

  //Manage log in state
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  return (
    <section className={`${auth.page} ${styles.page}`}>
      <div className={`${auth.card} ${styles.card}`}>
        <header>
          <p className={auth.brand}>SpendCents</p>
          <h1>Sign in</h1>
        </header>
        <form className={`${auth.form} ${styles.form}`} onSubmit={handleSubmit} aria-busy={isLoading}>
          <label>
            Username
            <input name="username" autoComplete="username" required onChange={handleChange} />
          </label>
          <label>
            Password
            <input name="password" type="password" autoComplete="current-password" required onChange={handleChange} />
          </label>
          {error && (
            <p role="alert">
              {"data" in error && isBackendError(error.data)
                ? error.data.description
                : "Couldn’t sign in. Please try again."}
            </p>
          )}
          <button type="submit" disabled={isLoading}>
            {isLoading ? "Signing in…" : "Sign in"}
          </button>
        </form>
        <p className={auth.footer}>New to SpendCents? <Link to="/register">Create an account</Link></p>
      </div>
    </section>
  );
}

export default LogIn;
