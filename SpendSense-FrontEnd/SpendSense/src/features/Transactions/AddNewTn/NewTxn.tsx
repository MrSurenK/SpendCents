import styles from "./NewTxn.module.css";
import InputBox from "../../../components/input-box/InputBox";
import Button from "../../../components/btn/Button";
import { useState } from "react";
import { createPortal } from "react-dom";
import {
  useAddNewTxnMutation,
  useGetCategoriesWithFullResQuery,
  type NewTransactionRequest,
  type TransactionType,
} from "../../../redux/rtk-queries/transactionService";
import Modal from "../../../components/modal/Modal";
import NewCatModal from "../../../components/modal/NewCatModal";

//ToDo: fix the date formating before submitting form

//Single source of truth for a blank form, used on mount and after a successful submit
const getEmptyForm = (): NewTransactionRequest => ({
  amount: 0,
  title: "",
  remarks: "",
  recurring: false,
  date: new Date().toISOString().slice(0, 10),
  categoryId: 0,
});

export default function NewTxn() {
  //State to manage form information

  const [newTxnForm, setNewTxnForm] =
    useState<NewTransactionRequest>(getEmptyForm);

  const [transactionType, setTransactionType] = useState<TransactionType>();

  const [fillAllFieldsMsg, setFillAllFieldsMsg] = useState<string | null>(null);
  const hasSelectedCategory = newTxnForm.categoryId !== 0;
  const handleCatChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const rawValue = e.target.value;
    const selectedCategoryId = rawValue === "" ? 0 : Number(rawValue);

    // Update form state with selected category id.
    setNewTxnForm((prev) => ({ ...prev, categoryId: selectedCategoryId }));

    //Update transactionType state to display the correct state
    const selectedCat = catData?.data?.find(
      (cat) => cat.id === selectedCategoryId,
    );
    setTransactionType(selectedCat?.transactionType);
  };

  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >,
  ) => {
    const { name, value } = e.target;

    let parsedValue: string | number | boolean = value;

    if (name === "amount" || name === "categoryId") {
      parsedValue = value === "" ? 0 : Number(value);
    } else if (name === "recurring") {
      parsedValue = value === "true";
    }
    setNewTxnForm((prev) => ({ ...prev, [name]: parsedValue }));
  };

  function formatToDdMmYyyy(dateStr: string) {
    const date = new Date(dateStr);
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = String(date.getFullYear());
    const day = String(date.getDate()).padStart(2, "0");
    console.log(`$(day) + $(month) + $(year)`);
    return `${day}-${month}-${year}`;
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    //Capture the form element before awaiting; e.currentTarget is null after the handler returns
    const form = e.currentTarget;

    console.log("Form before submit: ", newTxnForm);

    //Check if all fields have been filled
    if (
      newTxnForm.title.trim() === "" ||
      newTxnForm.remarks.trim() === "" ||
      newTxnForm.categoryId === 0 ||
      newTxnForm.amount <= 0
    ) {
      setFillAllFieldsMsg("PLEASE FILL ALL FIELDS!");
      return; // do not call API if all fields are not set
    }

    try {
      const payload = {
        ...newTxnForm,
        date: formatToDdMmYyyy(newTxnForm.date),
      };
      await addNewTxn(payload).unwrap(); //call add txn api

      //Reset the form after a successful submit so the user can add the next transaction.
      //form.reset() clears the uncontrolled inputs (amount, title, remarks, recurring radio),
      //while the state reset clears the controlled fields (date, category) and app state.
      form.reset();
      setNewTxnForm(getEmptyForm());
      setTransactionType(undefined);
      setFillAllFieldsMsg(null);

      setShowModal(true);
    } catch (error) {
      console.error("Failed to create transaction", error);
    }
    // console.log(newTxnForm);
  };

  //API call to get categories
  const { data: catData } = useGetCategoriesWithFullResQuery();

  const [addNewTxn] = useAddNewTxnMutation();

  //Todo: Categories is where the type is derived from so in the form user should not select type before selecting a category
  /*
   * If user creates a new categories ensure that the Cat API updates the state and calls again as a dependency in the service layer
   *
   */

  //Modal state management
  const [showModal, setShowModal] = useState(false);
  const [showCatModal, setShowCatModal] = useState(false);

  return (
    <>
      <div className={styles.pageBackground}>
        <div className={styles.receipt}>
          {/* ── Header ── */}
          <h1 className={styles.header}>New Transaction</h1>
          <p className={styles.storeName}>SpendSense Receipt</p>

          <hr className={styles.divider} />

          <form className={styles.form} onSubmit={handleSubmit}>
            {/* ── Transaction Type toggle ── */}
            <div className={styles.row}>
              <label>Type</label>
              <div
                className={styles.typeToggleWrap}
                data-tooltip={
                  hasSelectedCategory
                    ? undefined
                    : "Select a category first or create a new one"
                }
              >
                <div className={styles.typeDisplay}>
                  <div
                    className={`${styles.typeBox} ${
                      !hasSelectedCategory ? styles.typeBoxMuted : ""
                    } ${
                      transactionType === "expense"
                        ? styles.typeBoxActiveExpense
                        : ""
                    }`}
                  >
                    Expense
                  </div>
                  <div
                    className={`${styles.typeBox} ${
                      !hasSelectedCategory ? styles.typeBoxMuted : ""
                    } ${
                      transactionType === "income"
                        ? styles.typeBoxActiveIncome
                        : ""
                    }`}
                  >
                    Income
                  </div>
                </div>
              </div>
            </div>
            {/* ── Amount ── */}
            <div className={styles.row}>
              <label>Amount</label>
              <div className={styles.amountGroup}>
                <span className={styles.dollarSign}>$</span>
                <input
                  type="number"
                  name="amount"
                  placeholder="0.00"
                  step="0.01"
                  min="0"
                  onChange={handleInputChange}
                />
              </div>
            </div>
            <hr className={styles.divider} />
            {/* ── Title ── */}
            <div className={styles.row}>
              <label>Title</label>
              <InputBox
                name="title"
                onChange={handleInputChange}
                placeholder="e.g. Groceries"
                size="md"
              />
            </div>

            {/* ── Remarks ── */}
            <div className={styles.row}>
              <label>Remarks</label>
              <textarea
                name="remarks"
                className={styles.remarks}
                placeholder="Optional notes..."
                rows={2}
                onChange={handleInputChange}
              />
            </div>

            <hr className={styles.divider} />

            {/* ── Date ── */}
            <div className={styles.row}>
              <label>Date</label>
              <InputBox
                name="date"
                type="date"
                size="md"
                value={newTxnForm.date}
                onChange={handleInputChange}
              />
            </div>

            {/* ── Category ── */}
            <div className={styles.row}>
              <label>Category</label>
              <div className={styles.selectWrap}>
                <select
                  name="categoryId"
                  value={
                    newTxnForm.categoryId === 0 ? "" : newTxnForm.categoryId
                  }
                  onChange={handleCatChange}
                >
                  <option value="">Select a category</option>
                  {catData?.data?.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name} ({cat.transactionType})
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* ── Recurring ── */}
            <div className={styles.row}>
              <label>Recurring</label>
              <div className={styles.radioGroup}>
                <label>
                  <input
                    type="radio"
                    name="recurring"
                    value="true"
                    onChange={handleInputChange}
                  />
                  Yes
                </label>
                <label>
                  <input
                    type="radio"
                    name="recurring"
                    value="false"
                    defaultChecked
                    onChange={handleInputChange}
                  />
                  No
                </label>
              </div>
            </div>
            <hr className={styles.divider} />
            {/* ── Totals area (visual receipt touch) ── */}
            <div className={styles.totals}>
              <div className={styles.totalsRow}>
                <span>TOTAL</span>
                <span>${(newTxnForm.amount ?? 0).toFixed(2)}</span>
              </div>
            </div>
            <hr className={styles.divider} />
            {/* ── Actions ── */}
            <div className={styles.footer}>
              {fillAllFieldsMsg && (
                <h3 style={{ color: "red", textAlign: "center" }}>
                  {fillAllFieldsMsg}
                </h3>
              )}
              <Button text="Submit Transaction" size="lg" type="submit" />
              <Button
                text="Add New Category"
                variant="btn-secondary"
                size="sm"
                type="button"
                onClick={() => setShowCatModal(true)}
              />
            </div>
          </form>

          {/* ── Decorative barcode ── */}
          <div className={styles.barcode}>
            {Array.from({ length: 40 }).map((_, i) => (
              <span key={i} />
            ))}
          </div>
        </div>
      </div>
      {showModal && (
        <Modal
          message="Transaction has been successfully added!"
          setShowModal={setShowModal}
        ></Modal>
      )}
      {showCatModal &&
        createPortal(
          <NewCatModal setOpenCatModal={setShowCatModal} />,
          document.body,
        )}
    </>
  );
}
