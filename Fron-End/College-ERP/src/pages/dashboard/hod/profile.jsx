import { useEffect, useState } from "react";
import axios from "axios";
import { Card, CardBody, Typography, Button } from "@material-tailwind/react";

const API = "http://localhost:8080/api";

export function Profile() {
  const [hod, setHod] = useState(null); const [form, setForm] = useState({}); const [message, setMessage] = useState(""); const [error, setError] = useState("");
  const load = async () => {
    const id = localStorage.getItem("hodId");
    if (!id) return setError("No HOD session found. Please sign in again.");
    try { const { data } = await axios.get(`${API}/hods/${id}`); setHod(data); setForm({...data, password:"", subjects:(data.subjects||[]).join(", ")}); localStorage.setItem("hodData", JSON.stringify(data)); }
    catch (e) { setError(e.response?.data || "Unable to load HOD information."); }
  };
  useEffect(() => { load(); }, []);
  if (!hod) return <div className="mt-12">Loading HOD information...</div>;
  const save = async (e) => {
    e.preventDefault(); setMessage(""); setError("");
    try { const { data } = await axios.put(`${API}/hods/${hod.id}`, {...form, subjects:String(form.subjects||"").split(",").map(x=>x.trim()).filter(Boolean), password:form.password||undefined}); setHod(data); setForm({...data,password:"",subjects:(data.subjects||[]).join(", ")}); localStorage.setItem("hodData",JSON.stringify(data)); setMessage("Your profile was updated successfully."); }
    catch(e){setError(e.response?.data||"Could not update profile.");}
  };
  return <div className="mt-8 space-y-6"><Card><CardBody><div className="flex items-center gap-4"><div className="flex h-16 w-16 items-center justify-center rounded-full bg-blue-500 text-2xl font-bold text-white">{hod.name?.charAt(0).toUpperCase()}</div><div><Typography variant="h4">{hod.name}</Typography><Typography color="blue-gray">HOD • {hod.department}</Typography></div></div></CardBody></Card>
    <Card><CardBody><Typography variant="h5" className="mb-4">My Information</Typography><form onSubmit={save} className="grid gap-4 md:grid-cols-2">
      {[["name","Full Name"],["department","Department"],["username","Username"],["email","Email"],["phone","Phone"],["subjects","Subjects (comma separated)"]].map(([key,label])=><label key={key} className="text-sm font-medium">{label}<input className="mt-1 w-full rounded-lg border px-3 py-2" value={form[key]||""} onChange={e=>setForm({...form,[key]:e.target.value})} /></label>)}
      <label className="text-sm font-medium">New Password (optional)<input type="password" className="mt-1 w-full rounded-lg border px-3 py-2" value={form.password||""} onChange={e=>setForm({...form,password:e.target.value})} /></label>
      <div className="md:col-span-2"><Button type="submit">Save Changes</Button></div>
    </form>{message&&<p className="mt-4 text-green-600">{message}</p>}{error&&<p className="mt-4 text-red-600">{error}</p>}</CardBody></Card>
  </div>;
}
export default Profile;
