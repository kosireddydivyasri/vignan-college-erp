import { useEffect, useState } from "react";
import axios from "axios";

export default function AttendanceProfile() {
  const [professor, setProfessor] = useState(null);
  const [error, setError] = useState("");
  useEffect(() => {
    const id = localStorage.getItem("professorId");
    if (!id) { setError("No professor session found. Please sign in again."); return; }
    axios.get(`http://localhost:8080/api/professors/${id}`)
      .then(({ data }) => setProfessor(data))
      .catch((e) => setError(e.response?.data || "Unable to load professor information."));
  }, []);
  if (error) return <div className="mt-8 rounded bg-red-50 p-4 text-red-700">{error}</div>;
  if (!professor) return <div className="mt-8">Loading professor information...</div>;
  const subjects = Array.from(new Set([...(professor.subjects || []), professor.subject].filter(Boolean)));
  return <div className="mx-auto mt-8 max-w-4xl rounded-xl bg-white p-6 shadow">
    <h2 className="text-2xl font-bold">Professor Attendance Profile</h2>
    <p className="mt-1 text-sm text-gray-600">This page uses the currently signed-in professor account. No demo lecturer list is used.</p>
    <div className="mt-6 grid gap-4 md:grid-cols-2">
      <div><span className="text-sm text-gray-500">Professor</span><p className="font-semibold">{professor.name}</p></div>
      <div><span className="text-sm text-gray-500">Professor ID</span><p className="font-semibold">{professor.professorId}</p></div>
      <div><span className="text-sm text-gray-500">Department</span><p className="font-semibold">{professor.departmentName || "Not assigned"}</p></div>
      <div><span className="text-sm text-gray-500">Subjects</span><p className="font-semibold">{subjects.length ? subjects.join(", ") : "No subjects assigned yet"}</p></div>
    </div>
  </div>;
}
