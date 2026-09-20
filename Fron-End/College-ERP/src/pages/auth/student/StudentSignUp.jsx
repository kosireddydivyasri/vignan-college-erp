import { Card, Input, Checkbox, Button, Typography, Select, Option } from "@material-tailwind/react";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";

const DEFAULT_BRANCHES = [
  "Computer Science and Engineering",
  "Information Technology",
  "Electronics and Communication Engineering",
  "Electrical and Electronics Engineering",
  "Mechanical Engineering",
  "Civil Engineering",
  "Artificial Intelligence and Data Science",
];

export function StudentSignUp() {
  const [form, setForm] = useState({
    studentId: "", username: "", password: "", email: "", studName: "",
    studFatherName: "", studLastName: "", studentAge: "", studRollNo: "",
    year: "", studPhoneNumber: "", major: "",
  });
  const [branches, setBranches] = useState(DEFAULT_BRANCHES);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    axios.get("http://localhost:8080/api/departments/get-dept")
      .then(({ data }) => {
        const names = data.map((d) => d.name).filter(Boolean);
        if (names.length) setBranches(names);
      })
      .catch(() => setBranches(DEFAULT_BRANCHES));
  }, []);

  const update = (key, value) => setForm((prev) => ({ ...prev, [key]: value }));

  const handleSignUp = async (e) => {
    e.preventDefault(); setError(""); setSuccess("");
    const required = ["studentId", "username", "password", "email", "studName", "studFatherName", "studLastName", "studentAge", "studRollNo", "year", "studPhoneNumber", "major"];
    if (required.some((key) => !String(form[key]).trim())) return setError("Please fill in all required fields.");
    if (Number(form.studentAge) < 15 || Number(form.studentAge) > 100) return setError("Enter a valid age.");
    if (Number(form.year) < 1 || Number(form.year) > 4) return setError("B.Tech year must be between 1 and 4.");
    if (form.password.length < 6) return setError("Password must be at least 6 characters long.");

    setLoading(true);
    try {
      await axios.post("http://localhost:8080/api/students/add-student", {
        ...form,
        studentAge: Number(form.studentAge),
        studRollNo: Number(form.studRollNo),
        year: Number(form.year),
      });
      setSuccess("Student registered successfully. You can sign in now.");
      setTimeout(() => navigate("/auth/student/sign-in"), 700);
    } catch (err) {
      setError(err.response?.data || "Error saving student data. Please try again.");
    } finally { setLoading(false); }
  };

  return (
    <section className="m-4 flex justify-center">
      <Card className="w-full max-w-2xl p-6">
        <div className="text-center">
          <Typography variant="h2" className="font-bold mb-2">Student Sign Up</Typography>
          <Typography variant="paragraph" color="blue-gray">Register without profile photos or unnecessary personal fields.</Typography>
        </div>
        <form className="mt-6 space-y-5" onSubmit={handleSignUp}>
          <div>
            <Typography variant="h5" className="mb-3">Personal Information</Typography>
            <div className="grid gap-4 md:grid-cols-2">
              <Input label="First Name *" value={form.studName} onChange={(e) => update("studName", e.target.value)} required />
              <Input label="Last Name *" value={form.studLastName} onChange={(e) => update("studLastName", e.target.value)} required />
              <Input label="Father's Name *" value={form.studFatherName} onChange={(e) => update("studFatherName", e.target.value)} required />
              <Input label="Age *" type="number" min="15" max="100" value={form.studentAge} onChange={(e) => update("studentAge", e.target.value)} required />
              <Input label="Phone Number *" value={form.studPhoneNumber} onChange={(e) => update("studPhoneNumber", e.target.value)} required />
            </div>
          </div>

          <div>
            <Typography variant="h5" className="mb-3">Academic Information</Typography>
            <div className="grid gap-4 md:grid-cols-2">
              <Input label="Student ID *" value={form.studentId} onChange={(e) => update("studentId", e.target.value)} required />
              <Input label="Roll Number *" type="number" value={form.studRollNo} onChange={(e) => update("studRollNo", e.target.value)} required />
              <Select label="Branch / Major *" value={form.major} onChange={(value) => update("major", value || "")}>
                {branches.map((branch) => <Option key={branch} value={branch}>{branch}</Option>)}
              </Select>
              <Input label="B.Tech Year (1-4) *" type="number" min="1" max="4" value={form.year} onChange={(e) => update("year", e.target.value)} required />
            </div>
          </div>

          <div>
            <Typography variant="h5" className="mb-3">Account Information</Typography>
            <div className="grid gap-4 md:grid-cols-2">
              <Input label="Username *" value={form.username} onChange={(e) => update("username", e.target.value)} required />
              <Input label="Email *" type="email" value={form.email} onChange={(e) => update("email", e.target.value)} required />
              <Input label="Password *" type="password" value={form.password} onChange={(e) => update("password", e.target.value)} required />
            </div>
          </div>

          <Checkbox required label={<span>I agree to the Terms and Conditions.</span>} />
          <Button fullWidth type="submit" disabled={loading}>{loading ? "Registering..." : "Register Student"}</Button>
          {error && <p className="text-sm text-red-600">{error}</p>}
          {success && <p className="text-sm text-green-600">{success}</p>}
        </form>
        <Typography variant="small" color="gray" className="mt-4 flex justify-center">
          Already have an account?
          <Link to="/auth/student/sign-in" className="ml-1 font-bold text-blue-500">Sign In</Link>
        </Typography>
      </Card>
    </section>
  );
}
export default StudentSignUp;
