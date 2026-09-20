import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { Card, CardBody, Typography, Button } from "@material-tailwind/react";

const API = "http://localhost:8080/api";
const BRANCHES = [
  "Computer Science and Engineering", "Information Technology",
  "Electronics and Communication Engineering", "Electrical and Electronics Engineering",
  "Mechanical Engineering", "Civil Engineering", "Artificial Intelligence and Data Science",
];

const emptyStudent = { studentId: "", username: "", password: "", email: "", major: "", year: 1, studRollNo: "", studName: "", studFatherName: "", studLastName: "", studPhoneNumber: "", studentAge: "" };
const emptyProfessor = { professorId: "", name: "", subject: "", departmentName: "", username: "", password: "", email: "", subjects: "" };

function Field({ label, value, onChange, type = "text", required = false }) {
  return <label className="block text-sm font-medium text-blue-gray-700"><span>{label}{required ? " *" : ""}</span><input className="mt-1 w-full rounded-lg border border-blue-gray-200 px-3 py-2 outline-none focus:border-blue-500" type={type} value={value ?? ""} onChange={(e) => onChange(e.target.value)} required={required} /></label>;
}

export function Home() {
  const [tab, setTab] = useState("students");
  const [students, setStudents] = useState([]); const [professors, setProfessors] = useState([]); const [departments, setDepartments] = useState([]);
  const [student, setStudent] = useState(emptyStudent); const [professor, setProfessor] = useState(emptyProfessor); const [department, setDepartment] = useState("");
  const [editingStudent, setEditingStudent] = useState(null); const [editingProfessor, setEditingProfessor] = useState(null); const [editingDepartment, setEditingDepartment] = useState(null);
  const [message, setMessage] = useState(""); const [error, setError] = useState(""); const [loading, setLoading] = useState(false);

  const load = async () => {
    try {
      const [s, p, d] = await Promise.all([axios.get(`${API}/students`), axios.get(`${API}/professors/get-prof`), axios.get(`${API}/departments/get-dept`)]);
      setStudents(s.data); setProfessors(p.data); setDepartments(d.data);
    } catch (e) { setError(e.response?.data || "Could not load HOD management data. Is the backend running?"); }
  };
  useEffect(() => { load(); }, []);
  const stats = useMemo(() => ({ students: students.length, professors: professors.length, departments: departments.length }), [students, professors, departments]);
  const clearNotice = () => { setMessage(""); setError(""); };

  const saveStudent = async (e) => {
    e.preventDefault(); clearNotice();
    if (!editingStudent && student.password.trim().length < 6) { setError("A password of at least 6 characters is required when creating a student."); return; }
    if (Number(student.year) < 1 || Number(student.year) > 4) { setError("B.Tech year must be between 1 and 4."); return; }
    if (Number(student.studentAge) < 15 || Number(student.studentAge) > 100) { setError("Enter a valid age between 15 and 100."); return; }
    setLoading(true);
    try {
      const payload = { ...student, year: Number(student.year), studRollNo: Number(student.studRollNo), studentAge: Number(student.studentAge) };
      if (editingStudent && !student.password.trim()) delete payload.password;
      if (editingStudent) await axios.put(`${API}/students/${editingStudent.id}`, payload); else await axios.post(`${API}/students/add-student`, payload);
      setMessage(editingStudent ? "Student updated successfully." : "Student created successfully."); setStudent({...emptyStudent}); setEditingStudent(null); await load();
    } catch (e) { setError(e.response?.data || "Could not save student."); } finally { setLoading(false); }
  };

  const saveProfessor = async (e) => {
    e.preventDefault(); clearNotice();
    if (!editingProfessor && professor.password.trim().length < 6) { setError("A password of at least 6 characters is required when creating a professor."); return; }
    setLoading(true);
    try {
      const payload = { ...professor, subjects: professor.subjects.split(",").map((x) => x.trim()).filter(Boolean) };
      if (editingProfessor && !professor.password.trim()) delete payload.password;
      if (editingProfessor) await axios.put(`${API}/professors/by-id/${editingProfessor.id}`, payload); else await axios.post(`${API}/professors/add-prof`, payload);
      setMessage(editingProfessor ? "Professor updated successfully." : "Professor created successfully."); setProfessor({...emptyProfessor}); setEditingProfessor(null); await load();
    } catch (e) { setError(e.response?.data || "Could not save professor."); } finally { setLoading(false); }
  };

  const saveDepartment = async (e) => {
    e.preventDefault(); clearNotice(); setLoading(true);
    try {
      if (!department.trim()) return setError("Department name is required.");
      if (editingDepartment) await axios.put(`${API}/departments/${editingDepartment.id}`, { name: department }); else await axios.post(`${API}/departments/add-dept`, { name: department });
      setMessage(editingDepartment ? "Department updated." : "Department created."); setDepartment(""); setEditingDepartment(null); await load();
    } catch (e) { setError(e.response?.data || "Could not save department."); } finally { setLoading(false); }
  };

  const remove = async (url, label) => {
    if (!window.confirm(`Delete this ${label}? This cannot be undone.`)) return;
    clearNotice(); try { await axios.delete(url); setMessage(`${label} deleted.`); await load(); } catch (e) { setError(e.response?.data || `Could not delete ${label}.`); }
  };

  return <div className="mt-8 space-y-6">
    <div><Typography variant="h4" color="blue-gray">HOD Management Console</Typography><Typography className="mt-1 text-blue-gray-600">Manage students, professors and department branches from one place.</Typography></div>
    {(message || error) && <div className={`rounded-lg p-3 text-sm ${error ? "bg-red-50 text-red-700" : "bg-green-50 text-green-700"}`}>{error || message}</div>}
    <div className="grid gap-4 md:grid-cols-3">
      {[['Students', stats.students], ['Professors', stats.professors], ['Departments', stats.departments]].map(([title, value]) => <Card key={title}><CardBody><Typography variant="small" color="blue-gray">{title}</Typography><Typography variant="h3" color="blue-gray">{value}</Typography></CardBody></Card>)}
    </div>
    <div className="flex flex-wrap gap-2">
      {[['students','Students'],['professors','Professors'],['departments','Departments']].map(([key,label]) => <Button key={key} variant={tab === key ? "filled" : "outlined"} onClick={() => { setTab(key); clearNotice(); }}>{label}</Button>)}
    </div>

    {tab === "students" && <Card><CardBody>
      <Typography variant="h5" className="mb-4">{editingStudent ? "Edit Student" : "Add Student"}</Typography>
      <form onSubmit={saveStudent} className="grid gap-4 md:grid-cols-3">
        <Field label="Student ID" value={student.studentId} onChange={(v) => setStudent({...student, studentId:v})} required />
        <Field label="Roll Number" type="number" value={student.studRollNo} onChange={(v) => setStudent({...student, studRollNo:v})} required />
        <Field label="First Name" value={student.studName} onChange={(v) => setStudent({...student, studName:v})} required />
        <Field label="Last Name" value={student.studLastName} onChange={(v) => setStudent({...student, studLastName:v})} required />
        <Field label="Father's Name" value={student.studFatherName} onChange={(v) => setStudent({...student, studFatherName:v})} required />
        <Field label="Age" type="number" value={student.studentAge} onChange={(v) => setStudent({...student, studentAge:v})} required />
        <Field label="Phone" value={student.studPhoneNumber} onChange={(v) => setStudent({...student, studPhoneNumber:v})} required />
        <Field label="Email" type="email" value={student.email} onChange={(v) => setStudent({...student, email:v})} required />
        <Field label="Username" value={student.username} onChange={(v) => setStudent({...student, username:v})} required />
        <Field label={editingStudent ? "Password (optional)" : "Password"} type="password" value={student.password} onChange={(v) => setStudent({...student, password:v})} required={!editingStudent} />
        <label className="block text-sm font-medium"><span>Branch / Major *</span><select className="mt-1 w-full rounded-lg border px-3 py-2" value={student.major} onChange={(e) => setStudent({...student, major:e.target.value})} required><option value="">Select branch</option>{Array.from(new Set([...BRANCHES, ...departments.map(d=>d.name)])).map(b=><option key={b}>{b}</option>)}</select></label>
        <Field label="B.Tech Year" type="number" value={student.year} onChange={(v) => setStudent({...student, year:v})} required />
        <div className="md:col-span-3 flex gap-2"><Button type="submit" disabled={loading}>{loading ? "Saving..." : editingStudent ? "Update Student" : "Add Student"}</Button>{editingStudent && <Button type="button" variant="outlined" onClick={() => {setEditingStudent(null);setStudent(emptyStudent);}}>Cancel</Button>}</div>
      </form>
      <div className="mt-8 overflow-x-auto"><table className="w-full min-w-[900px] text-left"><thead><tr>{['ID','Name','Branch','Year','Username','Email','Actions'].map(h=><th key={h} className="border-b p-3 text-sm">{h}</th>)}</tr></thead><tbody>{students.map(s=><tr key={s.id} className="border-b"><td className="p-3">{s.studentId}</td><td className="p-3">{s.studName} {s.studLastName}</td><td className="p-3">{s.major}</td><td className="p-3">{s.year}</td><td className="p-3">{s.username}</td><td className="p-3">{s.email}</td><td className="p-3"><div className="flex gap-2"><Button size="sm" variant="outlined" onClick={() => {setEditingStudent(s);setStudent({...emptyStudent,...s,password:""});}}>Edit</Button><Button size="sm" color="red" onClick={() => remove(`${API}/students/${s.id}`, "student")}>Delete</Button></div></td></tr>)}</tbody></table></div>
    </CardBody></Card>}

    {tab === "professors" && <Card><CardBody>
      <Typography variant="h5" className="mb-4">{editingProfessor ? "Edit Professor" : "Add Professor"}</Typography>
      <form onSubmit={saveProfessor} className="grid gap-4 md:grid-cols-3">
        <Field label="Professor ID" value={professor.professorId} onChange={(v)=>setProfessor({...professor,professorId:v})} required />
        <Field label="Name" value={professor.name} onChange={(v)=>setProfessor({...professor,name:v})} required />
        <label className="block text-sm font-medium text-blue-gray-700"><span>Department *</span><select className="mt-1 w-full rounded-lg border border-blue-gray-200 px-3 py-2 outline-none focus:border-blue-500" value={professor.departmentName} onChange={(e)=>setProfessor({...professor,departmentName:e.target.value})} required><option value="">Select department</option>{departments.map(d=><option key={d.id} value={d.name}>{d.name}</option>)}</select></label>
        <Field label="Primary Subject" value={professor.subject} onChange={(v)=>setProfessor({...professor,subject:v})} required />
        <Field label="Username" value={professor.username} onChange={(v)=>setProfessor({...professor,username:v})} required />
        <Field label="Email" type="email" value={professor.email} onChange={(v)=>setProfessor({...professor,email:v})} required />
        <Field label="Subjects (comma separated)" value={professor.subjects} onChange={(v)=>setProfessor({...professor,subjects:v})} />
        <Field label={editingProfessor ? "Password (optional)" : "Password"} type="password" value={professor.password} onChange={(v)=>setProfessor({...professor,password:v})} required={!editingProfessor} />
        <div className="md:col-span-3 flex gap-2"><Button type="submit" disabled={loading}>{loading ? "Saving..." : editingProfessor ? "Update Professor" : "Add Professor"}</Button>{editingProfessor && <Button type="button" variant="outlined" onClick={()=>{setEditingProfessor(null);setProfessor(emptyProfessor);}}>Cancel</Button>}</div>
      </form>
      <div className="mt-8 overflow-x-auto"><table className="w-full min-w-[900px] text-left"><thead><tr>{['ID','Name','Department','Subject','Username','Email','Actions'].map(h=><th key={h} className="border-b p-3 text-sm">{h}</th>)}</tr></thead><tbody>{professors.map(p=><tr key={p.id} className="border-b"><td className="p-3">{p.professorId}</td><td className="p-3">{p.name}</td><td className="p-3">{p.departmentName}</td><td className="p-3">{p.subject}</td><td className="p-3">{p.username}</td><td className="p-3">{p.email}</td><td className="p-3"><div className="flex gap-2"><Button size="sm" variant="outlined" onClick={()=>{setEditingProfessor(p);setProfessor({...emptyProfessor,...p,subjects:(p.subjects||[]).join(", "),password:""});}}>Edit</Button><Button size="sm" color="red" onClick={()=>remove(`${API}/professors/${p.id}`,"professor")}>Delete</Button></div></td></tr>)}</tbody></table></div>
    </CardBody></Card>}

    {tab === "departments" && <Card><CardBody>
      <Typography variant="h5" className="mb-4">{editingDepartment ? "Edit Branch / Department" : "Add Branch / Department"}</Typography>
      <form onSubmit={saveDepartment} className="flex flex-col gap-3 md:flex-row"><div className="flex-1"><Field label="Department / Branch Name" value={department} onChange={setDepartment} required /></div><div className="flex items-end gap-2"><Button type="submit">{editingDepartment ? "Update" : "Add"}</Button>{editingDepartment && <Button type="button" variant="outlined" onClick={()=>{setEditingDepartment(null);setDepartment("");}}>Cancel</Button>}</div></form>
      <div className="mt-6 grid gap-3 md:grid-cols-2">{departments.map(d=><div key={d.id} className="flex items-center justify-between rounded-lg border p-4"><div><Typography className="font-semibold">{d.name}</Typography><Typography variant="small" color="blue-gray">Branch / Department</Typography></div><div className="flex gap-2"><Button size="sm" variant="outlined" onClick={()=>{setEditingDepartment(d);setDepartment(d.name);}}>Edit</Button><Button size="sm" color="red" onClick={()=>remove(`${API}/departments/${d.id}`,"department")}>Delete</Button></div></div>)}</div>
    </CardBody></Card>}
  </div>;
}
export default Home;
