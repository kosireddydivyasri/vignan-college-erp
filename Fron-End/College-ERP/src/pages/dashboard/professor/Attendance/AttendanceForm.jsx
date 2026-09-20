import { useEffect, useMemo, useState } from "react";
import axios from "axios";

const API = "http://localhost:8080/api";

export default function AttendanceForm() {
  const [professor, setProfessor] = useState(null);
  const [students, setStudents] = useState([]);
  const [subject, setSubject] = useState("");
  const [time, setTime] = useState("");
  const [attendanceDate, setAttendanceDate] = useState("");
  const [status, setStatus] = useState({});
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const subjects = useMemo(() => professor ? Array.from(new Set([...(professor.subjects || []), professor.subject].filter(Boolean))) : [], [professor]);

  useEffect(() => {
    const professorId = localStorage.getItem("professorId");
    if (!professorId) { setError("No professor session found. Please sign in again."); return; }
    Promise.all([axios.get(`${API}/professors/${professorId}`), axios.get(`${API}/students`)]).then(([p, s]) => {
      setProfessor(p.data);
      setSubject(Array.from(new Set([...(p.data.subjects || []), p.data.subject].filter(Boolean)))[0] || "");
      const department = (p.data.departmentName || "").trim().toLowerCase();
      setStudents((s.data || []).filter((student) => (student.major || "").trim().toLowerCase() === department));
    }).catch((e) => setError(e.response?.data || "Could not load attendance data."));
  }, []);

  const toggle = (id) => setStatus((prev) => ({ ...prev, [id]: prev[id] === "P" ? "A" : "P" }));

  const submit = async (e) => {
    e.preventDefault(); setMessage(""); setError("");
    if (!professor || !subject || !time || !attendanceDate) return setError("Select subject, date and time.");
    if (!students.length) return setError("No students are assigned to your department. Ask the HOD to add students to your department/branch.");
    try {
      const studentsMap = Object.fromEntries(students.map((s) => [s.studentId, status[s.studentId] || "A"]));
      await axios.post(`${API}/attendance/save`, { professor: professor.name, subject, time: `${time}:00`, attendanceDate, students: studentsMap });
      setMessage("Attendance saved successfully."); setStatus({});
    } catch (e) { setError(e.response?.data || "Could not save attendance."); }
  };

  return <div className="mx-auto mt-8 max-w-6xl rounded-xl bg-white p-6 shadow">
    <h2 className="text-2xl font-bold">Take Attendance</h2>
    <p className="mb-5 text-sm text-gray-600">Students are loaded from the database and limited to your department.</p>
    <form onSubmit={submit} className="grid gap-6 lg:grid-cols-2">
      <div className="space-y-4">
        <label className="block font-medium">Professor<input className="mt-1 w-full rounded border bg-gray-100 p-2" value={professor?.name || "Loading..."} readOnly /></label>
        <label className="block font-medium">Subject<select className="mt-1 w-full rounded border p-2" value={subject} onChange={(e) => setSubject(e.target.value)} required><option value="">Select subject</option>{subjects.map((s) => <option key={s}>{s}</option>)}</select></label>
        <label className="block font-medium">Date<input type="date" className="mt-1 w-full rounded border p-2" value={attendanceDate} onChange={(e) => setAttendanceDate(e.target.value)} required /></label>
        <label className="block font-medium">Time<input type="time" className="mt-1 w-full rounded border p-2" value={time} onChange={(e) => setTime(e.target.value)} required /></label>
      </div>
      <div><div className="mb-2 font-medium">Students ({students.length})</div><div className="max-h-96 space-y-2 overflow-auto rounded border bg-gray-50 p-4">{students.map((s) => <label key={s.id} className="flex cursor-pointer items-center gap-3 rounded bg-white p-2"><input type="checkbox" checked={status[s.studentId] === "P"} onChange={() => toggle(s.studentId)} /><span>{s.studName} {s.studLastName} <span className="text-xs text-gray-500">({s.studentId})</span></span><span className="ml-auto text-xs font-semibold">{status[s.studentId] === "P" ? "Present" : "Absent"}</span></label>)}{!students.length && <span className="text-sm text-gray-500">No department students found.</span>}</div></div>
      <div className="lg:col-span-2"><button className="rounded bg-blue-600 px-5 py-2 font-semibold text-white disabled:opacity-50" type="submit" disabled={!students.length}>Save Attendance</button>{message && <p className="mt-3 text-green-600">{message}</p>}{error && <p className="mt-3 text-red-600">{error}</p>}</div>
    </form>
  </div>;
}
