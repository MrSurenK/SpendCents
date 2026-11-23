import { NavLink, useLocation, useNavigate } from "react-router";
import styles from "./navBar.module.css";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useLogoutMutation } from "../../redux/rtk-queries/authService";
import { dashboardApi } from "../../redux/rtk-queries/dashboardService";
import { transactionApi } from "../../redux/rtk-queries/transactionService";
import { logoutState, selectAuthStatus } from "../../redux/slices/authSlice";

interface MenuItem {
  key: string | number;
  mainMenu: string;
  subMenu?: SubMenuItem[];
  path?: string;
}

type SubMenuItem = {
  key: string | number;
  menuItem: string;
  path: string;
};

export default function NavBar() {
  const menu: MenuItem[] = [
    {
      key: 1,
      mainMenu: "Dashboard",
      path: "dashboard",
    },
    {
      key: 2,
      mainMenu: "Transactions",
      subMenu: [
        {
          key: 21,
          menuItem: "View All Transactions",
          path: "/txn",
        },
        {
          key: 22,
          menuItem: "Add New Transactions",
          path: "/txn/addNew",
        },
      ],
    },
  ];

  //State for managing selected menu item
  const [toggleTransMenu, setToggleTransMenu] = useState<boolean>(false);

  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { isLoggedIn } = useSelector(selectAuthStatus);
  const [logout, { isLoading, isError }] = useLogoutMutation();

  async function handleLogout() {
    const result = await logout();
    if (result.error) return;

    dispatch(logoutState());
    navigate("/", { replace: true });
    dispatch(dashboardApi.util.resetApiState());
    dispatch(transactionApi.util.resetApiState());
  }

  const currPath = location.pathname; //gives curr path name
  const mainSegment = currPath.split("/")[1]; // first path segment

  return (
    <div className={styles.sideBar}>
      <h2 id={styles.main}>SpendCents</h2>
      <nav className={styles.nav} aria-labelledby={styles.main}>
        <ul className={styles.menuItems}>
          {menu.map((item) => {
            // Determine if this main menu should be active
            const isMainActive =
              item.path === mainSegment ||
              item.subMenu?.some((sub) => sub.path === currPath);

            return (
              <li key={item.key}>
                {item.subMenu ? (
                  <>
                    <button
                      type="button"
                      aria-expanded={toggleTransMenu}
                      onClick={() => setToggleTransMenu((prev) => !prev)}
                      className={`${styles.dropDownBtn} ${
                        isMainActive ? styles.active : ""
                      }`}
                    >
                      {item.mainMenu}
                    </button>
                    {toggleTransMenu && (
                      <ul>
                        {item.subMenu.map((sub) => {
                          const isSubActive = sub.path === currPath;
                          return (
                            <li
                              key={sub.key}
                              className={styles.dropDownContent}
                            >
                              <NavLink
                                to={sub.path}
                                className={`${styles.menuWords} ${
                                  isSubActive ? styles.active : ""
                                }`}
                              >
                                {sub.menuItem}
                              </NavLink>
                            </li>
                          );
                        })}
                      </ul>
                    )}
                  </>
                ) : (
                  <NavLink
                    to={`${item.path}`}
                    className={`${styles.menuWords} ${
                      isMainActive ? styles.active : ""
                    }`}
                  >
                    {item.mainMenu}
                  </NavLink>
                )}
              </li>
            );
          })}
        </ul>
      </nav>
      {isLoggedIn && (
        <div className={styles.sidebarFooter}>
          <button
            type="button"
            className={styles.logoutButton}
            disabled={isLoading}
            onClick={handleLogout}
          >
            {isLoading ? "Logging out…" : "Log out"}
          </button>
          {isError && (
            <p className={styles.logoutError} role="alert">
              Couldn’t log out. Please try again.
            </p>
          )}
        </div>
      )}
    </div>
  );
}
