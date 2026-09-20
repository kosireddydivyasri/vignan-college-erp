import { useEffect, useMemo, useState } from "react";
import axios from "axios";

export default function AttendanceView() {
  const [professor, setProfessor] = useState(null);
  const [subject, setSubject] = useState("");
  const [records, setRecords] = useState({});
  const [error, setError] = useState("");
  const subjects = useMemo(() => professor ? Array.from(new Set([...(professor.subjects || []), professor.subject].filter(Boolean))) : [], [professor]);
  useEffect(() => {
    const id = localStorage.getItem("professorId");
    if (!id) { setError("No professor session found. Please sign in again."); return; }
    axios.get(`http://localhost:8080/api/professors/${id}`).then(({ data }) => { setProfessor(data); setSubject(Array.from(new Set([...(data.subjects || []), data.subject].filter(Boolean)))[0] || ""); }).catch(e => setError(e.response?.data || "Unable to load professor information."));
  }, []);
  const load = async () => {
    if (!professor || !subject) return;
    try { const { data } = await axios.get("http://localhost:8080/api/attendance/lecturer/subject", { params: { lecturer: professor.name, subject } }); setRecords(data || {}); setError(""); }
    catch (e) { setError(e.response?.data || "Unable to load attendance history."); }
  };
  const dates = Object.keys(records).sort(); const students = {};
  dates.forEach(date => (records[date] || []).forEach(r => { students[r.studentName] = students[r.studentName] || {}; students[r.studentName][date] = r.status; }));
  return <div className="mx-auto mt-8 max-w-5xl rounded-xl bg-white p-6 shadow">
    <h2 className="text-2xl font-bold">View Attendance</h2>
    <p className="mb-5 text-sm text-gray-600">Only attendance created by the signed-in professor is shown.</p>
    {error && <div className="mb-4 rounded bg-red-50 p-3 text-red-700">{error}</div>}
    {professor && <div className="grid gap-4 md:grid-cols-2"><div><label className="block text-sm font-bold mb-2">Professor</label><input className="w-full rounded border bg-gray-100 p-2" value={professor.name} readOnly /></div><div><label className="block text-sm font-bold mb-2">Subject</label><select className="w-full rounded border p-2" value={subject} onChange={e=>setSubject(e.target.value)} required><option value="">Select your subject</option>{subjects.map(s=><option key={s}>{s}</option>)}</select></div></div>}
    <button onClick={load} disabled={!subject} className="mt-4 rounded bg-blue-600 px-4 py-2 font-semibold text-white disabled:opacity-50">Fetch Attendance</button>
    {subject && dates.length === 0 && <p className="mt-5 text-gray-600">No attendance records found for this subject.</p>}
    {dates.length > 0 && <div className="mt-6 overflow-x-auto"><table className="min-w-full border"><thead><tr><th className="border p-2 text-left">Student Name</th>{dates.map(d=><th key={d} className="border p-2">{new Date(d).toLocaleDateString("en-GB")}</th>)}</tr></thead><tbody>{Object.entries(students).map(([name,row])=><tr key={name}><td className="border p-2">{name}</td>{dates.map(d=><td key={d} className="border p-2 text-center">{row[d] || "-"}</td>)}</tr>)}</tbody></table></div>}
  </div>;
}
