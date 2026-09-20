import { Card, Input, Button, Typography, Select, Option } from "@material-tailwind/react";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";

export function HODSignUp() {
  const [departments, setDepartments] = useState([]);
  const [form, setForm] = useState({ name: "", department: "", username: "", password: "", email: "", phone: "", subjects: "" });
  const [error, setError] = useState(""); const [loading, setLoading] = useState(false); const [success, setSuccess] = useState("");
  const navigate = useNavigate();
  useEffect(() => { axios.get("http://localhost:8080/api/departments/get-dept").then(({ data }) => setDepartments(data)).catch(() => {}); }, []);
  const update = (key, value) => setForm((p) => ({ ...p, [key]: value }));
  const submit = async (e) => {
    e.preventDefault(); setError(""); setSuccess("");
    if (Object.entries(form).some(([k, v]) => k !== "subjects" && !String(v).trim())) return setError("Please fill in all required fields.");
    if (form.password.length < 6) return setError("Password must be at least 6 characters long.");
    setLoading(true);
    try {
      await axios.post("http://localhost:8080/api/hods/add-hod", { ...form, subjects: form.subjects.split(",").map((s) => s.trim()).filter(Boolean) });
      setSuccess("HOD registered successfully. You can sign in now."); setTimeout(() => navigate("/auth/hod/sign-in"), 700);
    } catch (err) { setError(err.response?.data || "Error saving HOD data."); } finally { setLoading(false); }
  };
  return <section className="m-4 flex justify-center"><Card className="w-full max-w-xl p-6">
    <div className="text-center"><Typography variant="h2" className="font-bold mb-2">HOD Sign Up</Typography><Typography variant="paragraph" color="blue-gray">Create the department head account.</Typography></div>
    <form className="mt-6 space-y-4" onSubmit={submit}>
      <Input label="Full Name *" value={form.name} onChange={(e) => update("name", e.target.value)} required />
      <Select label="Department *" value={form.department} onChange={(v) => update("department", v || "")}>
        {departments.map((d) => <Option key={d.id} value={d.name}>{d.name}</Option>)}
      </Select>
      <Input label="Username *" value={form.username} onChange={(e) => update("username", e.target.value)} required />
      <Input label="Email *" type="email" value={form.email} onChange={(e) => update("email", e.target.value)} required />
      <Input label="Phone *" value={form.phone} onChange={(e) => update("phone", e.target.value)} required />
      <Input label="Subjects (comma separated)" value={form.subjects} onChange={(e) => update("subjects", e.target.value)} />
      <Input label="Password *" type="password" value={form.password} onChange={(e) => update("password", e.target.value)} required />
      <Button fullWidth type="submit" disabled={loading}>{loading ? "Registering..." : "Register HOD"}</Button>
      {error && <p className="text-sm text-red-600">{error}</p>}{success && <p className="text-sm text-green-600">{success}</p>}
    </form>
    <Typography variant="small" color="gray" className="mt-4 flex justify-center">Already registered?<Link to="/auth/hod/sign-in" className="ml-1 font-bold text-blue-500">Sign In</Link></Typography>
  </Card></section>;
}
export default HODSignUp;
