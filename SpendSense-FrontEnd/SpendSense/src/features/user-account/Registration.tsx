import styles from "./Registration.module.css";
import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { useAppDispatch } from "../../hooks/reduxHooks";
import { loginState, type LoginPayload } from "../../redux/slices/authSlice";

interface RegistrationForm {
  email: string;
  userName: string;
  firstName: string;
  lastName: string;
  occupation: string;
  dob: string;
  password: string;
  matchPassword: string; // for validation only
}

type Payload = Omit<RegistrationForm, "matchPassword">;

function Registration() {
  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  //Construct the JSON from the form fields to pass to API
  const [formData, setFormData] = useState<RegistrationForm>({
    email: "",
    userName: "",
    firstName: "",
    lastName: "",
    occupation: "",
    dob: "",
    password: "",
    matchPassword: "",
  });

  //States to manage form validations
  const [matchError, setMatchError] = useState<string | null>(null);
  const [passSizeError, setPassSizeError] = useState<string | null>(null);
  const [requiredCheck, setRequiredCheck] = useState<string | null>(null);

  //API states for creating account
  const [loading, setLoading] = useState<boolean>(false);
  //ToDo: Modal to display error message
  const [submitError, setSubmitError] = useState<Error | null>(null);

  async function postForm(options: RequestInit) {
    setLoading(true);
    try {
      const res = await fetch(`${apiBaseUrl}/auth/register`, options);
      if (!res.ok) {
        //Catch HTTP errors
        const httpError = await res.text();
        console.log(`HTTP error. Status: ${res.status}, body:${httpError}`);
        throw new Error(`HTTP error. Status: ${res.status}, body:${httpError}`);
      }
      const result = await res.json().catch(() => null);
      return result;
    } catch (err) {
      setSubmitError(
        err instanceof Error ? err : new Error("Unknown error occurred"),
      );
    } finally {
      setLoading(false);
    }
  }

  async function logUserIn(
    username: string,
    password: string,
  ): Promise<LoginPayload | null> {
    setLoading(true);
    try {
      const res = await fetch(`${apiBaseUrl}/auth/login`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ username: username, password: password }),
      });
      if (!res.ok) {
        const httpErrorMsg = await res.text();
        throw new Error(
          `HTTP error. Status: ${res.status}, body: ${httpErrorMsg}`,
        );
      }
      const response = await res.json();

      return {
        userId: response.userId,
        username: response.username,
        lastLogin: new Date(response.lastLogin).toISOString(),
      };

      //Cookie expiration to match JWT access token expiration
    } catch (e) {
      setSubmitError(
        e instanceof Error
          ? e
          : new Error("Log in unsucessful.Please try again"),
      );
      return null;
    } finally {
      setLoading(false);
    }
  }

  // useEffect(() => {
  //   console.log(response?.message);
  // }, [response]);

  const checkPasswordMatch = (): boolean => {
    if (formData.matchPassword != formData.password) {
      setMatchError("Passwords do not match");
      return false;
    }
    setMatchError(null);
    return true;
  };

  const checkPasswordSize = (): boolean => {
    const size = formData.password.length;
    if (size < 8) {
      setPassSizeError("Use at least 8 characters for your password.");
      return false;
    }
    setPassSizeError(null);
    return true;
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    if (e.target.name === "matchPassword") {
      checkPasswordMatch();
    }
    if (e.target.name === "password") {
      checkPasswordSize();
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const { name, value } = e.target;
    // console.log(value);
    setFormData((prev) => ({
      ...prev,
      [name as keyof RegistrationForm]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent): Promise<void> => {
    e.preventDefault();
    //check if all fields have been filled
    if (
      !formData.email ||
      !formData.dob ||
      !formData.firstName ||
      !formData.lastName ||
      !formData.occupation ||
      !formData.password ||
      !formData.userName ||
      !formData.matchPassword
    ) {
      setRequiredCheck("Please fill in all required fields!");
      return;
    } else {
      setRequiredCheck(null);
    }

    if (!checkPasswordMatch() || !checkPasswordSize()) return;
    const payload: Payload = {
      email: formData.email,
      userName: formData.userName,
      firstName: formData.firstName,
      lastName: formData.lastName,
      occupation: formData.occupation,
      password: formData.password,
      dob: formData.dob.split("-").reverse().join("-"),
    };

    const options: RequestInit = {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    };

    const result = await postForm(options);

    if (result) {
      const loginPayload = await logUserIn(payload.userName, payload.password);
      if (loginPayload) {
        dispatch(loginState(loginPayload));
        navigate("/dashboard");
      }
    }
  };

  return (
    <section className={styles.page}>
      <div className={styles.card}>
        <header>
          <p className={styles.brand}>SpendCents</p>
          <h1>Create your account</h1>
        </header>
        <form className={styles.form} onSubmit={handleSubmit} aria-busy={loading}>
          <label>
            First name
            <input name="firstName" type="text" autoComplete="given-name" required onChange={handleChange} />
          </label>
          <label>
            Last name
            <input name="lastName" type="text" autoComplete="family-name" required onChange={handleChange} />
          </label>
          <label>
            Email
            <input name="email" type="email" autoComplete="email" required onChange={handleChange} />
          </label>
          <label>
            Username
            <input name="userName" type="text" autoComplete="username" required onChange={handleChange} />
          </label>
          <label>
            Occupation
            <input name="occupation" type="text" required onChange={handleChange} />
          </label>
          <label>
            Date of birth
            <input name="dob" type="date" autoComplete="bday" required onChange={handleChange} />
          </label>
          <label>
            Password
            <input name="password" type="password" autoComplete="new-password" required onChange={handleChange} onBlur={handleBlur} />
            {passSizeError && <span role="alert">{passSizeError}</span>}
          </label>
          <label>
            Confirm password
            <input name="matchPassword" type="password" autoComplete="new-password" required onChange={handleChange} onBlur={handleBlur} />
            {matchError && <span role="alert">{matchError}</span>}
          </label>
          {(requiredCheck || submitError) && (
            <p role="alert">{requiredCheck || submitError?.message}</p>
          )}
          <button disabled={loading} type="submit">
            {loading ? "Please wait…" : "Create account"}
          </button>
        </form>
        <p className={styles.footer}>Already have an account? <Link to="/">Sign in</Link></p>
      </div>
    </section>
  );
}

export default Registration;
