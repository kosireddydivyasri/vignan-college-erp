import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardBody, Typography, Button } from "@material-tailwind/react";

const API = "http://localhost:8080/api";
const BRANCHES = ["Computer Science and Engineering","Information Technology","Electronics and Communication Engineering","Electrical and Electronics Engineering","Mechanical Engineering","Civil Engineering","Artificial Intelligence and Data Science"];

export function Profile() {
  const [student, setStudent] = useState(null); const [form, setForm] = useState({}); const [message, setMessage] = useState(""); const [error, setError] = useState("");
  const load = async () => {
    const studentId = localStorage.getItem("studentId");
    if (!studentId) return setError("No student session found. Please sign in again.");
    try { const { data } = await axios.get(`${API}/students/${studentId}`); setStudent(data); setForm({...data,password:""}); localStorage.setItem("studentData",JSON.stringify(data)); }
    catch(e){setError(e.response?.data||"Unable to load your information.");}
  };
  useEffect(()=>{load();},[]);
  if(!student) return <div className="mt-12">Loading student information...</div>;
  const save = async (e) => { e.preventDefault(); setMessage(""); setError(""); try { const {data}=await axios.put(`${API}/students/${student.id}`,{...form,year:Number(form.year),studRollNo:Number(form.studRollNo),studentAge:Number(form.studentAge),password:form.password||undefined}); setStudent(data); setForm({...data,password:""}); localStorage.setItem("studentData",JSON.stringify(data)); setMessage("Your information was updated successfully."); } catch(e){setError(e.response?.data||"Could not update your information.");} };
  return <div className="mt-8 space-y-6"><Card><CardBody><div className="flex items-center gap-4"><div className="flex h-16 w-16 items-center justify-center rounded-full bg-blue-500 text-2xl font-bold text-white">{student.studName?.charAt(0).toUpperCase()}</div><div><Typography variant="h4">{student.studName} {student.studLastName}</Typography><Typography color="blue-gray">{student.major} • Year {student.year}</Typography></div></div></CardBody></Card>
    <Card><CardBody><Typography variant="h5" className="mb-4">My Information</Typography><form onSubmit={save} className="grid gap-4 md:grid-cols-2">
      {[["studName","First Name"],["studLastName","Last Name"],["studFatherName","Father's Name"],["studentAge","Age"],["studPhoneNumber","Phone"],["email","Email"],["username","Username"],["studRollNo","Roll Number"]].map(([key,label])=><label key={key} className="text-sm font-medium">{label}<input className="mt-1 w-full rounded-lg border px-3 py-2" type={key.includes("Age")||key==="studRollNo"?"number":key==="email"?"email":"text"} value={form[key]??""} onChange={e=>setForm({...form,[key]:e.target.value})} /></label>)}
      <label className="text-sm font-medium">Branch / Major<select className="mt-1 w-full rounded-lg border px-3 py-2" value={form.major||""} onChange={e=>setForm({...form,major:e.target.value})}>{Array.from(new Set([...(BRANCHES||[]),form.major].filter(Boolean))).map(b=><option key={b}>{b}</option>)}</select></label>
      <label className="text-sm font-medium">B.Tech Year<input type="number" min="1" max="4" className="mt-1 w-full rounded-lg border px-3 py-2" value={form.year??""} onChange={e=>setForm({...form,year:e.target.value})} /></label>
      <label className="text-sm font-medium">New Password (optional)<input type="password" className="mt-1 w-full rounded-lg border px-3 py-2" value={form.password||""} onChange={e=>setForm({...form,password:e.target.value})} /></label>
      <div className="md:col-span-2"><Button type="submit">Save Changes</Button></div>
    </form>{message&&<p className="mt-4 text-green-600">{message}</p>}{error&&<p className="mt-4 text-red-600">{error}</p>}</CardBody></Card></div>;
}
export default Profile;
